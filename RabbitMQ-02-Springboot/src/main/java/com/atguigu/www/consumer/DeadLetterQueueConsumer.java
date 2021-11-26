package com.atguigu.www.consumer;

import com.atguigu.www.config.TtlQueueConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 队列TTL 消费者
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {
    //接受QD队列的消息消息
    @RabbitListener(queues = TtlQueueConfig.DEAD_LETTER_QUEUE)
    public void receiveD(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("当前时间：{}，收到死信队列TTL为{}ms,消息:{}", new Date(),message.getMessageProperties().getReceivedDelay(), msg);
    }
}
