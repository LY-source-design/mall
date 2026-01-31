package pers.ly.mall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.ly.mall.common.constant.MqConstant;

@Configuration
public class SeckillMqConfig {

    @Bean
    public DirectExchange delaySeckillExchange() {
        return ExchangeBuilder
                .directExchange(MqConstant.SECKILL_DELAY_EXCHANGE)
                .delayed()
                .durable(true)
                .build();
    }

    @Bean
    public Queue delaySeckillQueue() {
        return QueueBuilder
                .durable(MqConstant.SECKILL_DELAY_QUEUE)
                .build();
    }

    @Bean
    public Binding delaySeckillBinding() {
        return BindingBuilder
                .bind(delaySeckillQueue())
                .to(delaySeckillExchange())
                .with(MqConstant.SECKILL_DELAY_KEY);
    }
}
