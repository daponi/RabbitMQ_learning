package com.atguigu.www.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列配置
 * 优化延时队列，增加队列QC
 */
@Configuration
public class TtlQueueConfig {
    //普通交换机名称
    public static final String X_EXCHANGE = "X";
    //死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    //普通队列的名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    //死信队列的名称
    public static final String DEAD_LETTER_QUEUE = "QD";

    // 声明 xExchange
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    // 声明 yExchange
    @Bean("yExchange")
    public DirectExchange yEchange() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明队列A且ttl为10s,并绑定到对应的死信交换机
    @Bean("queueA")
    public Queue queueA() {
        HashMap<String, Object> arguments = new HashMap<>();
        //声明当前队列绑定的死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由的routingKey
        arguments.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        arguments.put("x-message-ttl", 10000);

        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }

    //声明队列B且ttl为40s,并绑定到对应的死信交换机
    @Bean("queueB")
    public Queue queueB() {
        HashMap<String, Object> arguments = new HashMap<>();
        //声明当前队列绑定的死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由的routingKey
        arguments.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        arguments.put("x-message-ttl", 40000);

        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }

    //声明队列C且ttl由生产者设置，并绑定死信交换机
    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "YD");
        //ttl由生产者设置
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }
    //声明死信队列QD
    @Bean("queueD")
    public Queue queueD() {
        return new Queue(DEAD_LETTER_QUEUE);
    }

    // 队列QA绑定X交换机
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    // 队列QB绑定X交换机
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    // 队列QD绑定Y交换机
    @Bean
    public Binding deadLetterBindingY(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }

    // 队列QC绑定X交换机
    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC,@Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }
}


