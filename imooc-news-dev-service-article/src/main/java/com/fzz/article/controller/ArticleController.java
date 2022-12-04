package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.ArticleControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        Integer[] statusNums={1,2,3,4,5};
        List<Integer> statusList = Arrays.asList(statusNums);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(String.valueOf(status) )&& statusList.contains(status),
                Article::getArticleStatus,status);
        articleService.page(pageInfo,queryWrapper);
        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult createArticle(AddArticleBO addArticleBo, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        Date publishTime = addArticleBo.getPublishTime();
        if(publishTime==null){
            addArticleBo.setPublishTime(new Date());
        }
        Article article=new Article();
        BeanUtils.copyProperties(addArticleBo,article);
        if(addArticleBo.getArticleType()==2&&StringUtils.isNotBlank(addArticleBo.getArticleCover())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
        }else if(addArticleBo.getArticleType()==1){
            article.setArticleCover(null);
        }

        String str = redisUtil.get(REDIS_ALL_CATEGORY);
        if(StringUtils.isBlank(str)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }else{
            List<Category> categoryList = JsonUtils.jsonToList(str, Category.class);
            for(Category c:categoryList){
                if(c.getId()==article.getCategoryId()){
                    article.setArticleStatus(1);
                    boolean res = articleService.save(article);
                    if(res){
                        return GraceJSONResult.ok();
                    }
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
                }
            }
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
        }

    }

    @Override
    public GraceJSONResult queryMyList(Long userId, String keyword, Integer status,
                                       Date startDate, Date endDate, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        Integer[] statusNums={1,2,3,4,5};
        List<Integer> statusList = Arrays.asList(statusNums);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(userId!=null,Article::getPublishUserId,userId);
        queryWrapper.le(endDate!=null,Article::getPublishTime,endDate);
        queryWrapper.ge(startDate!=null,Article::getPublishTime,startDate);
        queryWrapper.eq(StringUtils.isNotBlank(String.valueOf(status) )&& statusList.contains(status),
                Article::getArticleStatus,status);
        articleService.page(pageInfo,queryWrapper);
        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult doReview(Long articleId, Integer passOrNot) {
        if(articleId!=null){
            Article article = articleService.getById(articleId);
            article.setArticleStatus(3);
            boolean res = articleService.updateById(article);
            if(res){
                return GraceJSONResult.ok();
            }
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
    }

    @Override
    public GraceJSONResult withdraw(Long articleId, Long userId) {
        if(articleId!=null&&userId!=null){
            boolean res=articleService.withdrawArticle(articleId,userId);
            if(res){
                return GraceJSONResult.ok();
            }
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
    }

    @Override
    public GraceJSONResult delete(Long articleId, Long userId) {
        if(articleId!=null&&userId!=null){
            boolean res = articleService.deleteArticle(articleId, userId);
            if(res){
                return GraceJSONResult.ok();
            }
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
    }


}
