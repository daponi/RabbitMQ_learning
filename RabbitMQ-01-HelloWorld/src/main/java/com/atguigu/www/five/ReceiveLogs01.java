package com.atguigu.www.five;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

/**
 * FANOUT交换机的消费者
 * ReceiveLogs01消费者： 消息接受
 * 其实老师没有说到重点，重点是这种类型是广播，即使key不相同的队列，也能获取
 * 这样不用区分先启动消费者还是先启动生产者
 */
public class ReceiveLogs01 {
    //交换机的名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtil.getChannel();
        //声明一个FANOUT交换机，者既可以在生产者声明也可以在消费者声明，消费者处声明防止先启动消费者找不到交换器而报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        /**
         * 声明一个临时队列, 队列的名称是随机的
         * 当消费者断开和该队列的连接时 队列自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        //把该临时队列与exchange绑定,其中 routingkey(也称之为 binding key)为空字符串
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        //接受消息的回调
        DeliverCallback deliverCallback=(consumerTag,  message)->{
            System.out.println("ReceiveLogs01接受到的消息:"+new String(message.getBody(), StandardCharsets.UTF_8));
        };

        //消费者
        channel.basicConsume(queueName, true, deliverCallback,cancelCallback ->{});

    }
}
