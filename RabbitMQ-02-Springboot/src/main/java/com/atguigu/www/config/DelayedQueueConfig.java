package com.atguigu.www.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延时队列的配置
 * 基于插件的延时队列
 */
@Configuration
public class DelayedQueueConfig {
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    //声明延时队列
    @Bean("delayedQueue")
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    //声明自定义交换机，类型是延时交换机
    @Bean("delayedExchange")
    public CustomExchange delayedExchange(){
        Map<String, Object> arguments = new HashMap<>();
        //自定义交换机与routingKey匹配的类型
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, arguments);
    }

    //绑定交换机和队列
    @Bean
    public Binding bindingDelayedQueue(@Qualifier("delayedQueue") Queue delayedQueue,@Qualifier("delayedExchange")CustomExchange delayedExchange){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}
