package com.fzz.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.AddArticleBO;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;

public interface ArticleService extends IService<Article> {

    boolean withdrawArticle(Long articleId, Long userId);

    boolean deleteArticle(Long articleId, Long userId);

    boolean createArticle(AddArticleBO addArticleBo, Category c);
}
