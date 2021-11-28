package com.atguigu.www.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 使用main方法演示优先级队列，因为启动Springboot时会将消费者一同启动,
 * 一起启动会造成发送一个消息就消费一个消息,不能体现出优先级队列排序的效果
 */
public class Producer {
    private final static String QUEUE_NAME = "hello22";

    public static void main(String[] args) {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.108.131");
        factory.setUsername("admin");
        factory.setPassword("1230123");

        //channel 实现了自动 close 接口 ,自动关闭 不需要显示关闭
        try (Connection connection = factory.newConnection();) {

            Channel channel = connection.createChannel();
            System.out.println("开始发送消息......");
            /**
             * 生成一个队列，且生成队列优先级
             */
            Map<String, Object> arguments = new HashMap<>();
            //设置队列优先级
            arguments.put("x-max-priority", 5);
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);

            /**
             * 发送一个消息，且生成消息优先级
             */
            for (int i = 1; i < 11; i++) {
                String message ="info "+i;
                if (i==3){
                    //设置消息优先级,且将消息设置持久化
                    AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().priority(5).contentType("text/plain").build();

                    channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
                } else if (i==5) {
                    //设置消息优先级,且消息持久化
                    AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().priority(3).contentType("text/plain").build();
                    channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
                }else {
                    //消息持久化
                    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                }
            }
            System.out.println("消息发送完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
