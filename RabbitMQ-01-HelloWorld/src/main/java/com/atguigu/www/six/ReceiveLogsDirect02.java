package com.atguigu.www.six;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * ReceiveLogs02消费者： 消息接受消息接受, 将接收到的err消息存储在磁盘
 * 将日志消息写入磁盘的程序仅接收严重错误(errros)，而不存储哪些警告(warning)或信息(info)日志消息避免浪费磁盘空间
 */
class ReceiveLogsDirect02 {
    //交换机的名称
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明一个Direct交换机，既可以在生产者声明也可以在消费者声明，消费者处声明防止先启动消费者找不到交换器而报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = "disk";
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        //把该队列与exchange绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        System.out.println("等待接收消息	");
        //接受到消息时的回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            message = "接收绑定键:" + delivery.getEnvelope().getRoutingKey() + ",消息:" + message;
            File file = new File("f:\\work\\rabbitmq_info.txt");
            FileUtils.writeStringToFile(file, message, StandardCharsets.UTF_8);
            System.out.println(message);
            System.out.println("错误日志已经接收");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}