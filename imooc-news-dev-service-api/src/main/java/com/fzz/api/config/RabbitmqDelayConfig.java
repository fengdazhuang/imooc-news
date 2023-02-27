package com.fzz.api.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqDelayConfig {

    public static final String EXCHANGE_DELAY="exchange_delay";

    public static final String QUEUE_DELAY="queue_delay";

    @Bean(EXCHANGE_DELAY)
    public Exchange delayExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_DELAY).delayed().durable(true).build();
    }

    @Bean(QUEUE_DELAY)
    public Queue delayQueue(){
        return QueueBuilder.durable(QUEUE_DELAY).build();
    }


    @Bean
    public Binding delayBinding(@Qualifier(EXCHANGE_DELAY) Exchange exchange,
                           @Qualifier(QUEUE_DELAY) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("delay.#").noargs();
    }


}
