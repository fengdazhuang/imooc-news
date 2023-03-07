package com.fzz.article.controller;

import com.fzz.api.config.RabbitmqDelayConfig;
import com.fzz.article.service.ArticleService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class ArticlePublishDelayConsumer {


    @Autowired
    private ArticleService articleService;



    @RabbitListener(queues = {RabbitmqDelayConfig.QUEUE_DELAY})
    public void watchArticlePublishDelay(Message message) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if(routingKey.equalsIgnoreCase("delay.publish.article")){
            String stringArticleId = new String(message.getBody());
            Long articleId = Long.valueOf(stringArticleId);
            System.out.println("时间："+new Date()+"--文章发布");
            articleService.updateDelayedArticle(articleId);
        }




    }


}
