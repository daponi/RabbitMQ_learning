package com.atguigu.www.thress;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 发送消息的生产者Demo
 */
public class Task02 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        //建立连接和信道
        Channel channel = RabbitMqUtil.getChannel();
        //使用发布确认
        channel.confirmSelect();
        //队列持久化标识
        boolean durable = true;
        //建立队列
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入消息:");
        while (scanner.hasNext()) {
            String message =  scanner.next();
            //发送消息，且消息实现持久化
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者已经发送消息："+message);

        }
    }
}
