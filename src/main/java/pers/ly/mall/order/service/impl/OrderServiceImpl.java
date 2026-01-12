package pers.ly.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.utils.RedisIdGeneratorUtils;
import pers.ly.mall.order.dto.CreateOrderDTO;
import pers.ly.mall.order.mapper.OrderMapper;
import pers.ly.mall.order.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    RedisIdGeneratorUtils redisIdGeneratorUtils;

    public OrderServiceImpl(RedisIdGeneratorUtils redisIdGeneratorUtils) {
        this.redisIdGeneratorUtils = redisIdGeneratorUtils;
    }
    /**
     * 生成订单
     */
    @Override
    public String createOrder(CreateOrderDTO createOrderDTO) {
        Long userId = CurrentContext.getUserId();
        Long carId = createOrderDTO.getCarId();
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        String orderNum = redisIdGeneratorUtils.nextId("order");

        //TODO:后续要更具购物车计算价格
        BigDecimal price = new BigDecimal("0");

        order.setUserId(userId);
        order.setOrderNumber(orderNum);
        order.setStatus((short)0);
        order.setCarId(carId);
        order.setPrice(price);
        order.setCreateTime(now);
        order.setUpdateTime(now);

        save(order);
        //TODO:整合RabbitMQ实现延迟队列,15分钟未支付直接判定为取消


        return orderNum;
    }
}
