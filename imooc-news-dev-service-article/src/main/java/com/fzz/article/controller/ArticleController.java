package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.ArticleControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.enums.ArticleStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

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
        }else{
            List<Category> categoryList = JsonUtils.jsonToList(str, Category.class);
            for(Category c:categoryList){
                if(c.getId()==addArticleBo.getCategoryId()){
                    boolean res = articleService.createArticle(addArticleBo,c);
                    if(res){
                        return GraceJSONResult.ok();
                        //ai审核





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
                    passOrNot==0?ArticleStatusEnum.FAILD.type() : ArticleStatusEnum.PUBLISH.type());
            boolean res = articleService.update(updateWrapper);
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
