package com.atguigu.www.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 连接工厂创建信道的工具类
 */
public class RabbitMqUtil {

    /**
     * 返回一个连接的channel
     * @return
     * @throws Exception
     */
    public static Channel getChannel() throws Exception{
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.108.131");
        factory.setUsername("admin");
        factory.setPassword("1230123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }
}
