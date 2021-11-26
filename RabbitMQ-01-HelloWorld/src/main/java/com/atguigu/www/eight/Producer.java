package com.atguigu.www.eight;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

/**
 * 死信队列Demo
 * 生产者
 */
public class Producer {
    //普通交换机名称
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //普通队列名称
    private static final String NORMAL_QUEUE = "normal_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明一个交换机，可以在生产者声明交换机，也可以在消费者声明，也可以在两端都声明
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明普通队列
/*        channel.queueDeclare(NORMAL_QUEUE, false, false, false, null);
        //绑定交换机和队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");*/
        //设置消息的 TTL,单位ms
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        //发送消息条数
        int messageCount = 10;
        for (int i = 0; i < messageCount; i++) {
            String message = "info" + i;
            //验证ttl过期进入死信
//            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", properties, message.getBytes(StandardCharsets.UTF_8));
            //验证队列达到最大长度进入死信
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("成功发送消息:"+message);
        }
    }
}
