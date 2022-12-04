package com.fzz.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.pojo.Article;

public interface ArticleService extends IService<Article> {

    boolean withdrawArticle(Long articleId, Long userId);

    boolean deleteArticle(Long articleId, Long userId);
}
