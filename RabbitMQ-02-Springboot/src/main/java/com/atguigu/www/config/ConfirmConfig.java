package com.atguigu.www.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，发布确认(高级)
 */
@Configuration
public class ConfirmConfig {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String CONFIRM_ROUTINGKEY= "key1";
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String WARNING_QUEUE_NAME = "warning.queue";

    //声明交换机,且绑定备份交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME).durable(true).withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME).build();
    }

    //声明备份交换机
    @Bean("backUpExchange")
    public FanoutExchange backUpExchange(){
        return ExchangeBuilder.fanoutExchange(BACKUP_EXCHANGE_NAME).build();
    }

    //声明队列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //声明备份队列
    @Bean("backUpQueue")
    public Queue backUpQueue(){return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();}

    // 声明警告队列
    @Bean("warningQueue")
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    //将交换机和队列绑定
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmExchange") DirectExchange directExchange,@Qualifier("confirmQueue") Queue queue){
        return BindingBuilder.bind(queue).to(directExchange).with(CONFIRM_ROUTINGKEY);
    }

    //将备份交换机和备份队列绑定，备份交换机是Fanout类型没有RoutingKey
    @Bean
    public Binding backUpBindingExchange(@Qualifier("backUpExchange") FanoutExchange fanoutExchange,@Qualifier("backUpQueue") Queue queue){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    //将报警队列和备份交换机绑定
    @Bean
    public Binding warningBinding(@Qualifier("warningQueue") Queue queue, @Qualifier("backUpExchange")FanoutExchange backupExchange){
        return BindingBuilder.bind(queue).to(backupExchange);
    }

}
