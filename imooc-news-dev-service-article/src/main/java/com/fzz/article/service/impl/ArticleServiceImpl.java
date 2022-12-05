package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.article.mapper.ArticleMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Override
    @Transactional
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
    @Transactional
    public boolean deleteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean createArticle(AddArticleBO addArticleBo, Category c) {
        Article article=new Article();
        BeanUtils.copyProperties(addArticleBo,article);
        article.setArticleStatus(1);
        if(addArticleBo.getIsAppoint()==0){
            article.setPublishTime(new Date());
        }else if(addArticleBo.getIsAppoint()==1){
            article.setPublishTime(addArticleBo.getPublishTime());
        }
        return this.save(article);
    }
}
