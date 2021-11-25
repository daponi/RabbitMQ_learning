package com.atguigu.www.thress;

import com.atguigu.www.util.RabbitMqUtil;
import com.atguigu.www.util.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.nio.charset.StandardCharsets;

/**
 * 消息在手动应答时是不丢失、消息自动重新入队列
 */
public class Worker03 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("C1等待接收消息处,理时间较短......");

        //消费者接受到消息接口回调逻辑
        DeliverCallback deliverCallback =( consumerTag,  message)->{
            String recMessage = new String(message.getBody(), StandardCharsets.UTF_8);
            //睡眠1秒模拟较快处理消息过程
            SleepUtils.sleep(1);
            System.out.println("C1接受到的消息:" + recMessage);
            /**
             * 手动应答
             * 1.消息的标记
             * 2.是否批量应答，false不批量应答信道中的信息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //消费者取消消费消息接口回调逻辑
        CancelCallback cancelCallback = consumerTag->{
            System.out.println("C1消费者取消消费接口回调逻辑:"+consumerTag);
        };

        //设置不公平分发
//        int prefetchCount =1 ;
        //设立预取值
        int prefetchCount =2 ;
        channel.basicQos(prefetchCount);
        //处理完消息手动应答标识
        boolean autoAck=false;

        //调用消费者
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
