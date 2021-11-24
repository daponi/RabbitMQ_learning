package com.atguigu.www.two;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 模拟多个消费者
 * Edit Configurations--Modify options--Allow multiple instances
 */
public class Worker01 {
    private static final String QUEUE_NAME = "message01";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();

        //接受到推送的消息时如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("成功接受到消息:" + message);
        };

        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑......");
        };
        /*
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者成功消费的回调
         * 4.消费者取消消费的回调
         */
        System.out.println("C3开始接受消息......");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
