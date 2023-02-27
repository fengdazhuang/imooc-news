package com.fzz.articlehtml;

import com.fzz.api.config.RabbitmqConfig;
import com.fzz.articlehtml.controller.ArticleHTMLComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RabbitMQConsumer {

    @Autowired
    private ArticleHTMLComponent articleHTMLComponent;

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_DOWNLOAD_HTML})
    public void watchDownloadQueue(Message message) throws Exception {
        String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
        if(receivedRoutingKey.equalsIgnoreCase("article.html.download")){
            String s = new String(message.getBody());
            System.out.println(s);
            String[] split = s.split(",");
            Long articleId=Long.valueOf(split[0]);
            String mongoFileId = split[1];
            System.out.println(articleId+"+"+mongoFileId);
            articleHTMLComponent.downloadArticleHTMLByMQ(articleId,mongoFileId);
        }else if(receivedRoutingKey.equalsIgnoreCase("article.html.delete")){
            Long articleId=Long.parseLong(new String(message.getBody()));
            System.out.println(articleId);
            articleHTMLComponent.deleteArticleHTMLByMQ(articleId);
        }else{
            System.out.println("不合法信息");
        }


    }


}
