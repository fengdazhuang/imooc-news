package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.RestTemplateService;
import com.fzz.api.controller.article.PortalControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.common.enums.ArticleStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.IPUtil;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.vo.ArticleDetailVO;
import com.fzz.vo.ShowArticleVO;
import com.fzz.vo.UserBaseInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class PortalController extends BaseController implements PortalControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplateService restTemplateService;

    @Override
    public GraceJSONResult listArticlesToUser(String keyword, Integer category, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsAppoint,0);
        String str = redisUtil.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = JsonUtils.jsonToList(str, Category.class);
        for(Category c:categoryList){
            if(c.getId()==category){
                queryWrapper.eq(Article::getCategoryId,category);
            }
        }
        queryWrapper.eq(Article::getArticleStatus, ArticleStatusEnum.PUBLISH.type());
        queryWrapper.orderByDesc(Article::getPublishTime);
        articleService.page(pageInfo,queryWrapper);

        List<Article> articleList = pageInfo.getRecords();

        //1:获得文章的作者id列表
        Set<Long> writerIdSet=new HashSet<>();
        List<String> keys=new ArrayList<>();
        for(Article article:articleList){
            keys.add(REDIS_ARTICLE_READ_COUNTS+":"+article.getId());
            writerIdSet.add(article.getPublishUserId());
        }

        //2:发起rest请求获取作者基本信息列表
        List<UserBaseInfoVO> list = restTemplateService.getUserBaseInfoListByIds(writerIdSet);
        Page<ShowArticleVO> articleVoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,articleVoPage,"records");

        //3:获取文章VO展示对象的列表
        if(list ==null||list.size()==0){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        List<String> readCountsList = redisUtil.mget(keys);

        AtomicInteger i= new AtomicInteger();
        List<ShowArticleVO> records = articleList.stream().map(((item -> {
            ShowArticleVO showArticleVo = new ShowArticleVO();
            BeanUtils.copyProperties(item,showArticleVo);
            UserBaseInfoVO userBaseInfoVO = getUserIfPublisher(showArticleVo.getPublishUserId(), list);
            showArticleVo.setPublisherVO(userBaseInfoVO);
            String value = readCountsList.get(i.getAndIncrement());
            int m=0;
            if(StringUtils.isNotBlank(value)){
                m=Integer.parseInt(value);
            }
            showArticleVo.setReadCounts(m);
            return showArticleVo;
        }))).collect(Collectors.toList());


        articleVoPage.setRecords(records);
        return GraceJSONResult.ok(articleVoPage);
    }


    /**
     * 根据发布者id在用户基本信息列表中查询
     * @param publisherUserId  发布者id
     * @param userBaseInfoVOList  用户基本信息列表
     * @return 用户基本信息
     */
    private UserBaseInfoVO getUserIfPublisher(Long publisherUserId,List<UserBaseInfoVO> userBaseInfoVOList){
        for(UserBaseInfoVO userBaseInfoVO:userBaseInfoVOList){
            if(userBaseInfoVO.getId().equals(publisherUserId)){
                return userBaseInfoVO;
            }
        }
        return null;
    }



    @Override
    public GraceJSONResult hotList() {
        return GraceJSONResult.ok(articleService.getHotList());
    }

    @Override
    public GraceJSONResult readArticle(Long articleId, HttpServletRequest request) {
        String requestIp = IPUtil.getRequestIp(request);
        String s = redisUtil.get(REDIS_ALREADY_READ + ":" + requestIp + ":" + articleId);
        if(StringUtils.isBlank(s)){
            redisUtil.increment(REDIS_ARTICLE_READ_COUNTS+":"+articleId,1);
            redisUtil.setnx60s(REDIS_ALREADY_READ+":"+requestIp+":"+articleId,String.valueOf(articleId));
        }
        return GraceJSONResult.ok();

    }

    @Override
    public GraceJSONResult showArticleDetail(Long articleId) {
        Article article=articleService.getArticleDetailById(articleId);
        if(article!=null){
            ArticleDetailVO articleDetailVO=new ArticleDetailVO();
            BeanUtils.copyProperties(article,articleDetailVO);
            Set<Long> set=new HashSet<>();
            set.add(article.getPublishUserId());
            List<UserBaseInfoVO> userBaseInfoListByIds = restTemplateService.getUserBaseInfoListByIds(set);
            articleDetailVO.setPublishUserName(userBaseInfoListByIds.get(0).getNickname());
            articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId));
            articleDetailVO.setCommentCounts(getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS+":"+articleId));
            return GraceJSONResult.ok(articleDetailVO);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_NOT_EXIST_ERROR);
    }


    @Override
    public Integer getReadCounts(Long articleId) {

        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId);
    }

    @Override
    public GraceJSONResult queryArticleOfWriter(Long writerId, Integer page, Integer pageSize) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsAppoint,0);
        queryWrapper.eq(Article::getPublishUserId,writerId);
        queryWrapper.eq(Article::getArticleStatus,ArticleStatusEnum.PUBLISH.type());
        queryWrapper.orderByDesc(Article::getPublishTime);
        articleService.page(pageInfo,queryWrapper);

        Page<ShowArticleVO> articleVoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,articleVoPage,"records");

        Set<Long> set=new HashSet<>();
        set.add(writerId);

        List<UserBaseInfoVO> list = restTemplateService.getUserBaseInfoListByIds(set);
        List<ShowArticleVO> records = pageInfo.getRecords().stream().map(((item -> {
            ShowArticleVO showArticleVo = new ShowArticleVO();
            BeanUtils.copyProperties(item,showArticleVo);
            showArticleVo.setPublisherVO(list.get(0));
            return showArticleVo;
        }))).collect(Collectors.toList());

        articleVoPage.setRecords(records);
        return GraceJSONResult.ok(articleVoPage);
    }

    @Override
    public GraceJSONResult queryGoodArticleOfWriter(Long writerId) {
        return GraceJSONResult.ok();
    }
}
