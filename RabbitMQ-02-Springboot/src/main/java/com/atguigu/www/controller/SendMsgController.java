package com.atguigu.www.controller;

import com.atguigu.www.config.DelayedQueueConfig;
import com.atguigu.www.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 发送延迟队列消息
 * http://localhost:8080/ttl/sendMsg/嘻嘻嘻
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发送消息的生产者
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message) {
        log.info("当前时间：{}，发送一条消息给两个TTL队列:{}", new Date(), message);
        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE, "XA", "消息来自TTL为10s的队列:" + message);
        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE, "XB", "消息来自TTL为40s的队列:" + message);
    }

    //发送消息且发送ttl的生产者
    @GetMapping("sendExpirationMsg/{message}/{ttlTime}")
    public void sendMSg(@PathVariable String message, @PathVariable String ttlTime) {
        log.info("当前时间：{}, 发送一条TTL时长:{}毫秒 , 信息给队列 C:{}", new Date(), ttlTime, message);
        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE, "XC", message, properties -> {
            properties.getMessageProperties().setExpiration(ttlTime);
            return properties;
        });
    }

    //基于插件的延时消息:使用延时队列、延时交换机的生产者
    @GetMapping("sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message, @PathVariable Integer delayTime) {
        log.info("当前时间：{},发送一条延迟{}毫秒的信息:{}给延时队列", new Date(), delayTime, message);
        //生产者设置延时时间,单位是ms
        MessagePostProcessor processor = msg -> {
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        };
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME, DelayedQueueConfig.DELAYED_ROUTING_KEY,
                message.getBytes(StandardCharsets.UTF_8), processor);
    }
}
