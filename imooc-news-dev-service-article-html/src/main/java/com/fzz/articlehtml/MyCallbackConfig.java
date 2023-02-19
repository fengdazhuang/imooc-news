package com.fzz.articlehtml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyCallbackConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已接收到id为：{} 的消息",id);
        }else{
            log.info("交换机未接收到id为{} 的消息，原因是：{}",id,cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息{} 未由交换机{} 的路由 {}接受,退回原因为：{}",new String(message.getBody()),exchange,routingKey,replyText);
    }
}
