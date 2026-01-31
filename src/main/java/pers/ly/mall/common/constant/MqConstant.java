package pers.ly.mall.common.constant;

public class MqConstant {
    //order的交换机
    public final static String ORDER_EXCHANGE = "mall.order.exchange";

    //延时检查订单delayMq
    public final static String ORDER_DELAY_KEY = "mall.order.delay.key";
    public final static String ORDER_DELAY_QUEUE = "mall.order.delay.queue";
    public final static String ORDER_DELAY_EXCHANGE = "mall.order.delay.exchange";
    public static final String HANDLE_DEAD_QUEUE = "mall.order.delay.consume.queue";

    //添加销量mq
    public static final String SALES_ADD_QUEUE = "mall.order.sales.queue";
    public static final String SALES_ADD_KEY = "mall.order.sales.key";

    //插件延迟队列
    public static final String SECKILL_DELAY_EXCHANGE = "skill.delay.exchange";
    public static final String SECKILL_DELAY_QUEUE = "skill.delay.queue";
    public static final String SECKILL_DELAY_KEY = "skill.delay.key";

    //订单生成
    public static final String ORDER_GENERATE_EXCHANGE = "order.generate.exchange";
    public static final String ORDER_GENERATE_QUEUE = "order.generate.queue";
    public static final String ORDER_GENERATE_KEY = "order.generate.key";
}
