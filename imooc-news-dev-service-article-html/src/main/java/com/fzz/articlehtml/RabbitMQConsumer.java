package com.fzz.articlehtml;

import com.fzz.api.config.RabbitmqConfig;
import com.fzz.articlehtml.controller.ArticleHTMLComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ArticleHTMLComponent articleHTMLComponent;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_DOWNLOAD_HTML})
    public void watchQueue(Message message) throws Exception {
        String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
        String string = Arrays.toString(message.getBody());
        String[] split = string.split(",");
        Long articleId=Long.parseLong(split[0]);
        String mongoFileId=split[1];
        articleHTMLComponent.downloadArticleHTML(articleId,mongoFileId);



    }
}
