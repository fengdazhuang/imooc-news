package com.fzz.api.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    public static final String EXCHANGE_ARTICLE="exchange_article";

    public static final String QUEUE_DOWNLOAD_HTML="queue_download_html";

    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_ARTICLE).durable(true).build();
    }

    @Bean(QUEUE_DOWNLOAD_HTML)
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_DOWNLOAD_HTML).build();
    }

    @Bean
    public Binding binding(@Qualifier(EXCHANGE_ARTICLE) Exchange exchange,
                           @Qualifier(QUEUE_DOWNLOAD_HTML) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("article.*.*").noargs();
    }


}
