package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.config.RabbitmqConfig;
import com.fzz.api.config.RabbitmqDelayConfig;
import com.fzz.api.controller.article.ArticleControllerApi;
import com.fzz.article.MyCallbackConfig;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.enums.ArticleStatusEnum;
import com.fzz.common.exception.CustomException;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.vo.ArticleDetailVO;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.models.auth.In;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${freemarker.html.targert}")
    private String articlePath;


    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getPublishTime);
        if(ArticleStatusEnum.isArticleStatusValid(status)){
            queryWrapper.eq(Article::getArticleStatus,status);
        }
        if(status!=null&&status==12){
            queryWrapper.eq(Article::getArticleStatus,ArticleStatusEnum.MANAGER_REVIEW.type())
                    .or().eq(Article::getArticleStatus,ArticleStatusEnum.AI_REVIEW.type());
        }
        articleService.page(pageInfo,queryWrapper);
        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult createArticle(AddArticleBO addArticleBo, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        if(addArticleBo.getArticleType()==1&&StringUtils.isBlank(addArticleBo.getArticleCover())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
        }else if(addArticleBo.getArticleType()==2){
            addArticleBo.setArticleCover(null);
        }
        String str = redisUtil.get(REDIS_ALL_CATEGORY);
        if(StringUtils.isBlank(str)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        List<Category> categoryList = JsonUtils.jsonToList(str, Category.class);
        for(Category c:categoryList){
            if(c.getId()==addArticleBo.getCategoryId()){

                boolean res = articleService.createArticle(addArticleBo);
                if(res){
                    return GraceJSONResult.ok();
                    //ai审核

                }
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
            }
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);


    }

    @Override
    public GraceJSONResult queryMyList(Long userId, String keyword, Integer status,
                                       Date startDate, Date endDate, Integer page, Integer pageSize) {
        if(userId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);

        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getPublishUserId,userId);
        queryWrapper.le(endDate!=null,Article::getCreatedTime,endDate)
                .ge(startDate!=null,Article::getCreatedTime,startDate);

        if(ArticleStatusEnum.isArticleStatusValid(status)){
            queryWrapper.eq(Article::getArticleStatus,status);
        }
        if(status!=null&&status==12){
            queryWrapper.eq(Article::getArticleStatus,ArticleStatusEnum.MANAGER_REVIEW)
                    .or().eq(Article::getArticleStatus,ArticleStatusEnum.AI_REVIEW);
        }
        articleService.page(pageInfo,queryWrapper);
        return GraceJSONResult.ok(pageInfo);
    }


    @Override
    public GraceJSONResult doReview(Long articleId, Integer passOrNot) {
        if(articleId!=null){
            LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(Article::getId,articleId);
            updateWrapper.set(Article::getArticleStatus,
                    passOrNot==ArticleStatusEnum.NOT_PASS.type()?ArticleStatusEnum.FAILD.type() : ArticleStatusEnum.PUBLISH.type());
            boolean res = articleService.update(updateWrapper);
            if(res){
                if(passOrNot==ArticleStatusEnum.PASS.type()){
                    try {
                        String mongoFileId = createArticleHTMLToGridFS(articleId);
                        updateArticleWithMongodb(articleId,mongoFileId);
                        //doDownloadArticleHTML(articleId,mongoFileId);

                        doDownloadArticleHTMLByMQ(articleId,mongoFileId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return GraceJSONResult.ok();
            }
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
    }





    @Autowired
    private GridFSBucket gridFSBucket;


    /**
     * 将文章上传至mongodb的Gridfs
     * @param articleId 文章id
     * @return mongodbId
     * @throws Exception
     */
    private String createArticleHTMLToGridFS(Long articleId) throws Exception{
        Configuration configuration = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        classpath = URLDecoder.decode(classpath, "utf-8");
        configuration.setDirectoryForTemplateLoading(new File(classpath+"templates"));

        Template template = configuration.getTemplate("detail.ftl", "utf-8");

        ArticleDetailVO articleDetail = getArticleDetail(articleId);
        Map<String ,Object> map=new HashMap<>();
        map.put("articleDetail",articleDetail);

        String string = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(string);

        ObjectId objectId = gridFSBucket.uploadFromStream("" + articleId, inputStream);

        return objectId.toString();

    }



    /**
     * 关联文章与mongodb
     * @param articleId 文章id
     * @param mongoFileId 文章对应的mongodbId
     * @return 状态码200
     */
    private void updateArticleWithMongodb(Long articleId,String mongoFileId){

        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,articleId);
        updateWrapper.set(Article::getMongoFileId,mongoFileId);
        articleService.update(updateWrapper);

    }

    /**
     * 下载存储在mongodb中的articlehtml文件
     * @param mongoFileId mongdbId
     * @throws Exception 异常
     */
    private void doDownloadArticleHTML(Long articleId, String mongoFileId) throws Exception {
        String url="http://html.imoocnews.com:8002/article/html/download?articleId="
                +articleId+"&mongoFileId="+mongoFileId;
        ResponseEntity<Integer> entity = restTemplate.getForEntity(url, Integer.class);
        Integer status = entity.getBody();
        if(!(status==HttpStatus.OK.value())){
            throw new CustomException(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

    }




    private void doDownloadArticleHTMLByMQ(Long articleId, String mongoFileId) {

        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_ARTICLE,"article.html.download",
                articleId+","+mongoFileId);
    }



    public ArticleDetailVO getArticleDetail(Long articleId){
        String path="http://article.imoocnews.com:8001/portal/article/detail?articleId="+articleId;
        ResponseEntity<GraceJSONResult> entity = restTemplate.getForEntity(path, GraceJSONResult.class);
        GraceJSONResult body = entity.getBody();
        ArticleDetailVO articleDetailVO=null;
        if(body.getStatus()==200){
            String json = JsonUtils.objectToJson(body.getData());
            articleDetailVO = JsonUtils.jsonToPojo(json, ArticleDetailVO.class);
        }
        return articleDetailVO;
    }

    @Override
    public GraceJSONResult withdraw(Long articleId, Long userId) {
        if(articleId!=null&&userId!=null){
            //deleteArticleHTML(articleId);
            Integer integer = deleteArticleHTMLByMQ(articleId);
            if(integer==200){
                boolean res=articleService.withdrawArticle(articleId,userId);
                if(res){

                    return GraceJSONResult.ok();
                }
            }

            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
    }

    @Override
    public GraceJSONResult delete(Long articleId, Long userId) {
        if(articleId!=null&&userId!=null){
            //deleteArticleHTML(articleId);
            Integer integer = deleteArticleHTMLByMQ(articleId);
            if(integer==200){
                boolean res = articleService.deleteArticle(articleId, userId);
                if(res){
                    return GraceJSONResult.ok();
                }
            }

            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
    }



    private void deleteArticleHTML(Long articleId){
        String url="http://html.imoocnews.com:8002/article/html/delete?articleId="+articleId;
        ResponseEntity<Integer> entity = restTemplate.getForEntity(url, Integer.class);
        Integer body = entity.getBody();
        if(!(body==HttpStatus.OK.value())){
            throw new CustomException(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
        String articleUrl=articlePath+File.separator+articleId+".html";
        File articleName = new File(articleUrl);
        if(articleName.exists()){
            articleName.delete();
        }
    }

    private Integer deleteArticleHTMLByMQ(Long articleId){

        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_ARTICLE,"article.html.delete",
                articleId+"");
        return HttpStatus.OK.value();

    }


}
