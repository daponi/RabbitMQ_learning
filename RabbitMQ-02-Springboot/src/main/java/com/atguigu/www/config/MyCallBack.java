package com.atguigu.www.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 实现交换机确认、消息确认的回调接口
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 由于ConfirmCallback接口是RabbitTemplate类的内部接口，所以重写后要注入到RabbitTemplate类里
     *
     * @PostConstruct是JSR250定义的注解，被注解的方法将在bean创建并且赋值完成，在执行初始化方法之前调用，原理：后置处理器起的作用
     */
    @PostConstruct
    public void init() {
        //注入
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 交换机确认回调方法
     * 1.发消息且换机按收到了回调方法
     *   1.1 correlationData保存回调消息的ID及相关信息
     *      1.2交换机收到消息 ack = true
     *      1.3 cause=null
     * 2.发送消息，交换机按收失败了回调
     *      2.1 correlationData 保存回调消息的ID及相关信息
     *      2.2交换机未收到消息 ack = false
     *      2.3 cause = 失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到id为:{}的消息", id);
        } else {
            log.info("交换机还未收到id为:{}消息,由于原因:{}", id, cause);
        }
    }

    /**
     * 用于消息到达交换机但没有正确的RoutingKey的队列
     * 消息会退的回调方法，只有不可到达目的地时才回退
     * 设置消息回调，若消息发送到错误RoutingKey时可以回调，防止不可路由丢失消息
     * 可以逻辑实现在当消息传递过程种不可达到目的地时，将消息返回生产者
     * @param returned
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("回退消息:{},被交换机退回，代码:{}，原因:{},路由key:{}",new String(returned.getMessage().getBody()),returned.getReplyCode(),returned.getReplyText(),returned.getRoutingKey());
    }
}
