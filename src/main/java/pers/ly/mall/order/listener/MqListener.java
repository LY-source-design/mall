package pers.ly.mall.order.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.entity.DelayMessage;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.order.service.OrderService;

@Slf4j
@Component
public class MqListener {
    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    public MqListener(RabbitTemplate rabbitTemplate,  OrderService orderService) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderService = orderService;
    }

    @RabbitListener(queues = MqConstant.HANDLE_DEAD_QUEUE)
    public void handleDeadLetterMessage(DelayMessage<Long> msg){
        //订单id
        Long id = msg.getMessage();
        log.info("检查订单id:" + id);
        //查询数据库
        Order order = orderService.query().eq("id", id).list().get(0);
        if(order != null){
            Short status = order.getStatus();
            if(Order.NOT_PAY.equals(status)){
                //没支付
                if(msg.hasNextDelay()) {
                    //重新发送给死信队列
                    Long delay = msg.removeNextDelay();
                    rabbitTemplate.convertAndSend(MqConstant.ORDER_EXCHANGE, MqConstant.DELAY_ORDER_ROUTING_KEY,
                            msg, new DelayMessageHandle(delay));
                }
                else {
                    //取消订单
                    log.info("取消id是{}订单", id);
                    orderService.update().set("status", Order.CANCEL).eq("id", id).update();
                }
            }
            else {
                log.info("id为{}的订单已支付", id);
            }
        }

    }


}
