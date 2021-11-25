package com.atguigu.www.six;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * Direct交换机的消费者
 * ReceiveLogs01消费者： 消息接受
 */
public class ReceiveLogsDirect01 {

    //交换机的名称
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明一个Direct交换机，既可以在生产者声明也可以在消费者声明，消费者处声明防止先启动消费者找不到交换器而报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "console";
        channel.queueDeclare(queueName, false, false, false, null);
        //把该队列与exchange绑定,此次console队列绑定两个RoutingKey
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        channel.queueBind(queueName, EXCHANGE_NAME, "warning");

        System.out.println("等待接收消息	");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 接收绑定键 :" + delivery.getEnvelope().getRoutingKey() + ", 消息:" + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}