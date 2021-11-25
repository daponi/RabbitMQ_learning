package com.atguigu.www.four;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 通过所耗费的时间来比较确认哪种方式是最好的
 * 1.单个确认   共耗时:740ms
 * 2.批量确认   共耗时:64ms
 * 3.异步确认   共耗时:27
 */
public class ConfirmMessage {
    private static int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //单个确认
//        ConfirmMessage.publishMessageIndividually();
        //批量确认
//        ConfirmMessage.publishMessageBatch();
        //异步确认
        ConfirmMessage.publishMessageAsync();
    }

    /**
     * 单个确认
     * 发布1000条单独确认消息，共耗时:740ms
     */
    public static void publishMessageIndividually() throws Exception {
        String queueName = UUID.randomUUID().toString();
        Channel channel = RabbitMqUtil.getChannel();
        //开启确认模式
        channel.confirmSelect();
        //建立队列
        channel.queueDeclare(queueName, true, false, false, null);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            //发布消息
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
//            boolean confirmFlag = channel.waitForConfirms();
            channel.waitForConfirms();
            //打印太耗时，先取消打印进行测试
//            if (confirmFlag) {
//                System.out.println(i+" 消息发送成功！");
//            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "条单独确认消息，共耗时:" + (end - begin) + "ms");
    }


    /**
     * 批量确认
     * 每发送100条消息就批量确认，共耗时:64ms
     */
    public static void publishMessageBatch() throws Exception {
        String queueName = UUID.randomUUID().toString();
        Channel channel = RabbitMqUtil.getChannel();
        //开启确认模式
        channel.confirmSelect();
        //建立队列
        channel.queueDeclare(queueName, true, false, false, null);

        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 1;

        long begin = System.currentTimeMillis();
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            String message = i + "";
            //发布消息
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            if (i % batchSize == 0) {
                channel.waitForConfirms();
                //取消耗时的打印
//              System.out.println("第"+outstandingMessageCount+"次确认！");
//              outstandingMessageCount++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "条消息而批量确认，共耗时:" + (end - begin) + "ms");
    }

    /**
     * 异步确认
     * 每发送100条消息就批量确认，共耗时:27ms
     */
    public static void publishMessageAsync() throws Exception {
        String queueName = UUID.randomUUID().toString();
        Channel channel = RabbitMqUtil.getChannel();
        //建立队列
        channel.queueDeclare(queueName, true, false, false, null);
        //开启确认模式
        channel.confirmSelect();

        /**
         * 记录异步未确认的信息
         * 代码主线程与监听器是两个线程，所需需要线程安全有序的一个哈希表，且适用于多线程，高并发的情况下
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除内容且只需要给到序号
         * 3.支持高并发(名线程)
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        /**
         * 消息确认成功的回调函数
         * 1.消息的序列号(标记)
         * 2.是否为批量确认 true时，就是批量监控，false就是单个监控
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //批量监控时则删除多个
            if (multiple) {
                //2. 删除已经确认到的消息，剩下的就是未确认的消息,返回的是小于等于当前序列号的消息组成一个 map，true代表等于，否则是小于
                ConcurrentNavigableMap<Long, String> confirmMap = outstandingConfirms.headMap(deliveryTag, true);
                confirmMap.clear();
            } else {
                //只清除当前序列号的消息
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认成功的消息:" + deliveryTag);
        };
        /**
         * 消息确认失败的回调函数
         * 1.消息的序列号(标记)
         * 2.是否为批量确认 true时，就是批量监控，false就是单个监控，
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //3. 打印出未确认的消息有哪些
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认成功的消息: " + message + ", 未确认成功的Tag: " + deliveryTag);
        };
        /**
         * 添加一个异步确认的监听器,监听哪些消息成功了哪些消息失败了，监听器就是一个线程，且是批量监听
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        long begin = System.currentTimeMillis();

        //循环里只管发消息，因为异步确认是由Broker自行处理的，Producer只需要用监听回调函数就行
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            String message = i + "【消息】";
            //1. 记录所有要发送的消息，记录了消息的总和，channel.getNextPublishSeqNo()获取下一个消息的序列号
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            //发布消息
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }

        long end = System.currentTimeMillis();
        //控制台被确认的消息肯定不到1000条,因为异步监听器是批量响应的，ConfirmCallback有multiple参数是true时批量监控，false时单个监控，若循环发布一次就睡一秒就它变成单个监控了
        System.out.println("发布" + MESSAGE_COUNT + "条消息而异步确认，共耗时:" + (end - begin) + "ms");
    }

}
