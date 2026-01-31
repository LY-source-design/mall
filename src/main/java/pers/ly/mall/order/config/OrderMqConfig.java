package pers.ly.mall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.ly.mall.common.constant.MqConstant;

@Configuration
public class OrderMqConfig {


    /**
     * return 路由订单id的交换机
     */
    @Bean
    public Exchange orderExchange() {
        return ExchangeBuilder
                .directExchange(MqConstant.ORDER_EXCHANGE)
                .build();
    }

    /**
     * return 添加销量的mq
     */
    @Bean
    public Queue salesQueue() {
        return QueueBuilder
                .durable(MqConstant.SALES_ADD_QUEUE)
                .build();
    }

    @Bean
    public Binding salesQueueBinding() {
        return BindingBuilder
                .bind(salesQueue())
                .to(orderExchange())
                .with(MqConstant.SALES_ADD_KEY)
                .noargs();
    }

    /**
     * @return 死信交换机
     */
    @Bean
    public FanoutExchange delayOrderExchange(){
        return ExchangeBuilder
                .fanoutExchange(MqConstant.ORDER_DELAY_EXCHANGE)
                .build();
    }

    /**
     * @return 死性队列
     */
    @Bean
    public Queue delayOrderQueue(){
        return QueueBuilder
                .durable(MqConstant.ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", MqConstant.ORDER_DELAY_EXCHANGE)
                .build();
    }

    /**
     * @return 处理死信的队列
     */
    @Bean
    public Queue consumeDeadLetterQueue(){
        return QueueBuilder
                .durable(MqConstant.HANDLE_DEAD_QUEUE)
                .build();
    }

    /**
     * @return 声明延迟队列和普通交换机的绑定关系
     */
    @Bean
    public Binding delayOrderBinding(){
        return BindingBuilder
                .bind(delayOrderQueue())
                .to(orderExchange())
                .with(MqConstant.ORDER_DELAY_KEY)
                .noargs();
    }

    /**
     * @return 死信交换机和处理死信的队列绑定
     */
    @Bean
    public Binding deadLetterBinding(){
        return BindingBuilder
                .bind(consumeDeadLetterQueue())
                .to(delayOrderExchange());
    }


}
