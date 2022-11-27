package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.controller.article.ArticleControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.Article;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ArticleController implements ArticleControllerApi {
    @Override
    public Object queryMyList(Long userId, String keyword, Integer status, Date startDate,
                              Date endDate, Integer page, Integer pageSize) {
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getArticleStatus,status);
        queryWrapper.eq(Article::getPublishUserId,userId);
        queryWrapper.ge(Article::getCreateTime,startDate);
        queryWrapper.le(Article::getCreateTime,endDate);

        return GraceJSONResult.ok(pageInfo);
    }
}
