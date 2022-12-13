package com.fzz.article.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fzz.article.service.ArticleService;
import com.fzz.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/*@Configuration
@EnableScheduling*/
public class ArticleScheduledService {

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0/3 * * * * ? ")
    public void publishArticle(){
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.ge(Article::getPublishTime, new Date());
        updateWrapper.set(Article::getIsAppoint,0);
        articleService.update(updateWrapper);
    }




}
