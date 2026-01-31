package pers.ly.mall.order.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.entity.DelayMessage;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.handler.DeadMessageHandler;
import pers.ly.mall.good.service.EsService;
import pers.ly.mall.good.service.GoodService;
import pers.ly.mall.order.service.OrderService;
import pers.ly.mall.order.vo.GoodQuantityVO;

import java.util.List;

@Slf4j
@Component
public class OrderMqListener {
    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    private final GoodService goodService;
    private final EsService esService;
    public OrderMqListener(RabbitTemplate rabbitTemplate, OrderService orderService,
                           GoodService goodService, EsService esService) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderService = orderService;
        this.goodService = goodService;
        this.esService = esService;
    }

    /**
     * 死信交换机处理订单计时功能
     * @param msg 消息
     */
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
                    rabbitTemplate.convertAndSend(MqConstant.ORDER_EXCHANGE, MqConstant.ORDER_DELAY_KEY,
                            msg, new DeadMessageHandler(delay));
                }
                else {
                    //取消订单
                    log.info("取消id是{}订单", id);
                    orderService.update().set("status", Order.CANCEL).eq("id", id).update();
                }
            }
            else if(Order.CANCEL.equals(status)){
                log.info("id为{}的订单已取消", id);
            }
            else if(Order.WAIT_TO_REACH.equals(status)){
                log.info("id为{}的订单已支付", id);
            }
        }
    }

    /**
     * 处理添加销量的信息
     * @param orderId 订单id
     */
    @Transactional
    @RabbitListener(queues = MqConstant.SALES_ADD_QUEUE)
    public void handleAddSalesMessage(Long orderId){
        List<GoodQuantityVO> goodIdWithQuantity = orderService.queryOrderInfoById(orderId);
        //mysql中添加销量
        log.info("开始更新mysql销量");
        orderService.addSales(goodIdWithQuantity);
        //es添加销量(乐观锁)
        log.info("开始更新es销量");
        esService.addSales(goodIdWithQuantity);
    }
}
