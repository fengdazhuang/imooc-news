package com.fzz.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.AddArticleBO;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;

import java.util.List;

public interface ArticleService extends IService<Article> {

    boolean withdrawArticle(Long articleId, Long userId);

    boolean deleteArticle(Long articleId, Long userId);

    boolean createArticle(AddArticleBO addArticleBo);

     void updateDelayedArticle(Long articleId);

    List<Article> getHotList();

    Article getArticleDetailById(Long articleId);
}
