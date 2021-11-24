package com.atguigu.www.thress;

import com.atguigu.www.util.RabbitMqUtil;
import com.atguigu.www.util.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * C2在30s模拟处理消息过程中若挂掉，则消息会重新入队列进而给其它消费者
 * 消息在手动应答时是不丢失、消息自动重新入队列
 */
public class Worker04 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("C2等待接收消息处,理时间较长......");

        //消费者接受到消息接口回调逻辑
        DeliverCallback deliverCallback =( consumerTag,  message)->{
            String recMessage = new String(message.getBody(), StandardCharsets.UTF_8);
            //睡眠30秒模拟较慢处理消息过程
            SleepUtils.sleep(30);
            System.out.println("C2接受到的消息:" + recMessage);
            /**
             * 手动应答
             * 1.消息的标记
             * 2.是否批量应答，false不批量应答信道中的信息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //消费者取消消费消息接口回调逻辑
        CancelCallback cancelCallback = consumerTag->{
            System.out.println("C2消费者取消消费接口回调逻辑:"+consumerTag);
        };

        //处理完消息手动应答标识
        boolean autoAck=false;

        //调用消费者
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
