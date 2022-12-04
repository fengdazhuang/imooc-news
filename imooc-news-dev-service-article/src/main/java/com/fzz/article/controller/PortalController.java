package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.admin.service.CategoryService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.PortalControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.pojo.AppUser;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.user.service.AppUserService;
import com.fzz.vo.PublisherVO;
import com.fzz.vo.ShowArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PortalController extends BaseController implements PortalControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AppUserService appUserService;

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
        Category categoryByName=null;
        if(category!=null){
            categoryByName = categoryService.getById(category);
            if(categoryByName==null){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
            queryWrapper.eq(Article::getCategoryId,category);
        }
        queryWrapper.eq(Article::getArticleStatus,3);
        articleService.page(pageInfo,queryWrapper);

        Page<ShowArticleVO> articleVoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,articleVoPage,"records");

        List<ShowArticleVO> records = pageInfo.getRecords().stream().map(((item -> {
            PublisherVO publisherVo = new PublisherVO();
            ShowArticleVO showArticleVo = new ShowArticleVO();
            BeanUtils.copyProperties(item,showArticleVo);
            Long publishUserId = item.getPublishUserId();
            publisherVo.setId(publishUserId);
            AppUser publisher = appUserService.getById(publishUserId);
            publisherVo.setFace(publisher.getFace());
            publisherVo.setNickname(publisher.getNickname());
            showArticleVo.setPublisherVO(publisherVo);
            return showArticleVo;

        }))).collect(Collectors.toList());
        articleVoPage.setRecords(records);

        return GraceJSONResult.ok(articleVoPage);
    }

    @Override
    public GraceJSONResult hotList() {
        return null;
    }

    @Override
    public GraceJSONResult showArticleDetail(Long articleId) {
        Article article=null;
        if(articleId!=null){
            article = articleService.getById(articleId);
        }
        if(article!=null){
            return GraceJSONResult.ok(article);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_NOT_EXIST_ERROR);
    }
}
