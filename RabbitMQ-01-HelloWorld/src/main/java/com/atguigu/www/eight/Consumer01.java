package com.atguigu.www.eight;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列
 * 消费者01,一般把交换机和队列定义在生产者更好
 */
public class Consumer01 {
    //普通交换机名称
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //普通队列名称
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //死信交换机名称
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明死信和普通交换机，类型为Direct,防止只启动消费者而没启动生产者时报错
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明普通队列，且设置死信交换机和死信的routingKey
        Map<String, Object> arguments = new HashMap<>();
        //过期时间ttl，单位为毫秒，一般在生产者处设置会更灵活，而在消费者处设置就一直不会变了
//        arguments.put("x-message-ttl", 10000);
        //正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key", "lisi");
        //设置正常队列的最大长度，多余的消息进入死信
//        arguments.put("x-max-length",6);

        //声明普通队列，且队列设置关于死信的arguments
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        //绑定普通队列与普通交换机
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");
        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        //绑定死信队列与死信交换机
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi", arguments);
        System.out.println("Consumer01等待接受消息......");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            //验证拒绝接受消息时且不再放入队列时，消息进入死信队列
            if (msg.equals("info5")){
                System.out.println("Consumer01接受到的消息是:" + msg+"，此消息被Consumer01拒绝！");
                //拒绝消息且不再放入队列，false表示不再放入队列
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            }else{
            System.out.println("Consumer01接受到的消息是:" + msg);
            //确认接受消息，false不开启批量确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        };
        //验证ttl过期、消息队列达到最大长度
//        channel.basicConsume(NORMAL_QUEUE, true, deliverCallback, cancelCallback -> {});
        //验证消息被拒绝且不放入队列时进入死信队列，false不能再开启自动确认
        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, cancelCallback -> {});
    }

}
