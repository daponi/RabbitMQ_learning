package com.atguigu.www.two;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 简单的生产者Demo
 */
public class Producter {
    private static final String QUEUE_NAME = "message01";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //从控制台当中接受信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            /**
             * 生成一个队列
             * 1.队列名称
             * 2.队列里面的消息是否持久化?默认消息不持久化存储在内存中
             * 3.该队列是否不进行共享只供一个消费者进行消费? true 只一个消费者消费
             * 4.是否自动删除？ 最后一个消费者端开连接以后，该队列是否自动删除？ true 自动删除
             * 5.其他参数
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(message+"消息发送完毕!");
        }

    }
}
