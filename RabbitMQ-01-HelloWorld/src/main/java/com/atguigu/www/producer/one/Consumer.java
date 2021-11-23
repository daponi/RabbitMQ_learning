package com.atguigu.www.producer.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 简单的消费者Demo
 */
public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.108.131");
        factory.setUsername("admin");
        factory.setPassword("1230123");

        //不能立即关闭连接，否则没执行完deliverCallback
//        try(Connection connection = factory.newConnection();) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            System.out.println("等待接受消息......");

            //接受到推送的消息时如何进行消费的接口回调
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody());
                System.out.println("成功接受到消息:" + message);
            };

            //取消消费的一个回调接口 如在消费的时候队列被删除掉了
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("消息消费被中断......");
            };
            /*
             *
             * 消费者消费消息
             * 1.消费哪个队列
             * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
             * 3.消费者成功消费的回调
             * 4.消费者取消消费的回调
             */
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
            //不能立即关闭连接，否则没执行完deliverCallback
            //connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
