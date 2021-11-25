package com.atguigu.www.five;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * FANOUT交换机的生产者
 * 生产者发送消息
 * 其实老师没有说到重点，重点是这种类型是广播，即使key不相同的队列，也能获取
 * 这样不用区分先启动消费者还是先启动生产者
 */
public class EmitLog {
    //交换机的名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明一个交换机，可以在生产者声明交换机，也可以在消费者声明，也可以在两端都声明
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入消息:");
        while (scanner.hasNext()) {
            String message =  scanner.next();
            //发布消息
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者成功发出消息："+message);
            
        }
    }
}
