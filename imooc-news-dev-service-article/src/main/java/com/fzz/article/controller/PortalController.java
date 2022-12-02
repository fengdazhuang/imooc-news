package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.admin.service.CategoryService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.PortalControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PortalController extends BaseController implements PortalControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public GraceJSONResult list(String keyword, String category, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        Category categoryByName=null;
        if(StringUtils.isNotBlank(category)){
            categoryByName = categoryService.queryCategoryByName(category);
            if(categoryByName==null){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
            queryWrapper.eq(StringUtils.isNotBlank(category),Article::getCategoryId,categoryByName.getId());

        }
        articleService.page(pageInfo,queryWrapper);
        List<Article> records = pageInfo.getRecords();

        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult hotList() {
        return null;
    }
}
