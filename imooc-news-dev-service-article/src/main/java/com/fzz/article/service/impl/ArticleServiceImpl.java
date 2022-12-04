package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.article.mapper.ArticleMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.common.exception.CustomException;
import com.fzz.pojo.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Override
    public boolean withdrawArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        Article article = this.getOne(queryWrapper);
        if(article!=null&&article.getArticleStatus()==3){
            article.setArticleStatus(5);
            return this.updateById(article);
        }
        return false;
    }

    @Override
    public boolean deleteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        return this.remove(queryWrapper);
    }
}
