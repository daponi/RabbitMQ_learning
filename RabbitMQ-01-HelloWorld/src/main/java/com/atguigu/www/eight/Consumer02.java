package com.atguigu.www.eight;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 死信队列
 * 消费者02
 */
public class Consumer02 {
    //死信交换机名称
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("Consumer02等待接受消息......");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Consumer02接受到的消息是:" + new String(message.getBody(), StandardCharsets.UTF_8));
        };
        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, cancelCallback -> {});
    }
}
