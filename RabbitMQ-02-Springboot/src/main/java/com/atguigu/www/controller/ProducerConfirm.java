package com.atguigu.www.controller;

import com.atguigu.www.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 开始发送消息，测试确认
 */
@Slf4j
@RestController
@RequestMapping("/confirm")
public class ProducerConfirm {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发消息
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        //发送到正确的交换机和RoutingKey
        //correlationData默认id是UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData("111");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTINGKEY, message,correlationData);
        log.info("发送的消息为:{}，路由Key为:{}",message,ConfirmConfig.CONFIRM_ROUTINGKEY);

        //发送到错误的交换机
        correlationData = new CorrelationData("222");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME+"err", ConfirmConfig.CONFIRM_ROUTINGKEY, message+"errExchange",correlationData);
        log.info("发送的消息为:{}，路由Key为:{}",message+"errExchange",ConfirmConfig.CONFIRM_ROUTINGKEY);

        //发送到错误的RoutingKey
        correlationData = new CorrelationData("333");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTINGKEY+"err", message+"errRoutingKey",correlationData);
        log.info("发送的消息为:{}，路由Key为:{}",message+"errRoutingKey",ConfirmConfig.CONFIRM_ROUTINGKEY);
    }
}
