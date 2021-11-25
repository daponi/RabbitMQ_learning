package com.atguigu.www.six;

import com.atguigu.www.util.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * DIRECT交换机的生产者
 */
public class EmitLogDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtil.getChannel()) {
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            //创建多个 bindingKey
            Map<String, String> bindingKeyMap = new HashMap<>();
            bindingKeyMap.put("info", " 普 通 info 信 息 ");
            bindingKeyMap.put("warning", " 警 告 warning 信 息 ");
            bindingKeyMap.put("error", "错误 error 信息");
            //debug 没有消费者接收这个消息 所有就丢失了
            bindingKeyMap.put("debug", "调试 debug 信息");
            for (Map.Entry<String, String> bindingKeyEntry : bindingKeyMap.entrySet()) {
                String bindingKey = bindingKeyEntry.getKey();
                String message = bindingKeyEntry.getValue();
                channel.basicPublish(EXCHANGE_NAME, bindingKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("生产者发出消息:" + message);
            }
        }
    }
}