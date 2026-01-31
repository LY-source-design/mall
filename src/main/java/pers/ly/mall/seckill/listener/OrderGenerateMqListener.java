package pers.ly.mall.seckill.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.entity.CarItem;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.entity.SeckillGood;
import pers.ly.mall.order.service.OrderService;
import pers.ly.mall.seckill.dto.SeckillOrderGenerateDTO;
import pers.ly.mall.seckill.service.SeckillGoodService;
import pers.ly.mall.shoppingcar.service.ShoppingCarService;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Component
public class OrderGenerateMqListener {
    private final OrderService orderService;
    private final ShoppingCarService shoppingCarService;
    private final RabbitTemplate rabbitTemplate;
    private final SeckillGoodService seckillGoodService;
    public
    OrderGenerateMqListener(OrderService orderService, ShoppingCarService shoppingCarService,
                            RabbitTemplate rabbitTemplate, SeckillGoodService seckillGoodService) {
        this.orderService = orderService;
        this.shoppingCarService = shoppingCarService;
        this.rabbitTemplate = rabbitTemplate;
        this.seckillGoodService = seckillGoodService;
    }

    /**
     * 异步生成订单
     * @param seckillOrderGenerateDTO 参数
     */
    @RabbitListener(queues = MqConstant.ORDER_GENERATE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void orderGenerateMqListener(SeckillOrderGenerateDTO seckillOrderGenerateDTO) {
        Long userId = seckillOrderGenerateDTO.getUserId();
        Long seckillId = seckillOrderGenerateDTO.getSeckillId();
        log.info("id为{}开始创建订单", userId);
        //查询抢购的商品信息
        SeckillGood seckillGood = seckillGoodService.query().select("good_id", "price").eq("id", seckillId).one();
        //创建并保存购物车
        CarItem carItem = new CarItem();
        carItem.setGoodId(seckillGood.getGoodId());
        carItem.setPrice(seckillGood.getPrice());
        carItem.setQuantity((short) 1); //暂时抢购默认一个
        Long carId = shoppingCarService.saveShoppingCar(Collections.singletonList(carItem), userId);
        //创建订单并保存订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(seckillOrderGenerateDTO.getOrderNumber());
        order.setCarId(carId);
        order.setPrice(seckillGood.getPrice());
        order.setStatus(Order.WAIT_TO_REACH);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderService.save(order);

        //添加销量
        rabbitTemplate.convertAndSend(MqConstant.ORDER_EXCHANGE, MqConstant.SALES_ADD_KEY, order.getId());
        log.info("id为{}订单创建成功", userId);
    }
}
