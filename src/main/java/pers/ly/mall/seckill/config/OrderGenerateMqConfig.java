package pers.ly.mall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.ly.mall.common.constant.MqConstant;

@Configuration
public class OrderGenerateMqConfig {
    @Bean
    public DirectExchange orderGenerateExchange() {
        return ExchangeBuilder
                .directExchange(MqConstant.ORDER_GENERATE_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderGenerateQueue() {
        return QueueBuilder
                .durable(MqConstant.ORDER_GENERATE_QUEUE)
                .build();
    }

    @Bean
    public Binding orderGenerateBinding() {
        return BindingBuilder.bind(orderGenerateQueue())
                .to(orderGenerateExchange())
                .with(MqConstant.ORDER_GENERATE_KEY);
    }
}
