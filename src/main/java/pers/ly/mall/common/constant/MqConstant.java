package pers.ly.mall.common.constant;

public class MqConstant {
    //order的交换机
    public final static String ORDER_EXCHANGE = "mall.order.exchange";

    //延时检查订单delayMq
    public final static String DELAY_ORDER_ROUTING_KEY = "mall.order.delay.key";
    public final static String DELAY_ORDER_QUEUE = "mall.order.delay.queue";
    public final static String DELAY_ORDER_EXCHANGE = "mall.order.delay.exchange";


    public static final String HANDLE_DEAD_QUEUE = "mall.order.delay.consume.queue";
}
