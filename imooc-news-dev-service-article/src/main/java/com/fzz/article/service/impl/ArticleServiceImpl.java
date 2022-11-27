package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.article.mapper.ArticleMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.pojo.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
}
