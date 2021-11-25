package com.atguigu.www.seven;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 声明主题交换机 及相关队列
 * 消费者C1
 */
public class ReceiveLogsTopic01 {
    //交换机名称
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName="Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        //把队列与exchange绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("C1消费者等待接受消息......");
        //接受到消息的回调
        DeliverCallback deliverCallback=(consumerTag,  delivery)->{
            System.out.println("接收到队列："+ queueName +",routingKey是:"+delivery.getEnvelope().getRoutingKey()
            +",消息是："+new String(delivery.getBody(), StandardCharsets.UTF_8));

        };
        //消费者接受消息
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
