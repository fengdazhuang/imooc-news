package com.fzz.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.vo.ShowArticleVO;

import java.util.List;

public interface ArticleService extends IService<Article> {

    boolean withdrawArticle(Long articleId, Long userId);

    boolean deleteArticle(Long articleId, Long userId);

    boolean createArticle(AddArticleBO addArticleBo);

     void updateDelayedArticle(Long articleId);

    List<Article> getHotList();

    Article getArticleDetailById(Long articleId);

    boolean doArticleReview(Long articleId, Integer passOrNot);

    GraceJSONResult getArticlesToUser(String keyword, Integer category, Integer page, Integer pageSize);

    GraceJSONResult getEsArticlesToUser(String keyword, Integer category, Integer page, Integer pageSize);

}
