package com.atguigu.www.consumer;

import com.atguigu.www.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者，发布确认(高级)
 */
@Slf4j
@Component
public class ConsumerConfirm {

    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message){
        log.info("消费者接受到confirmQueue队列的消息:{},路由Key:{}",new String(message.getBody()),message.getMessageProperties().getReceivedRoutingKey());
    }
}
