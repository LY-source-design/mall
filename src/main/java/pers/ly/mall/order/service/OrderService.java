package pers.ly.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.order.dto.CreateOrderDTO;

public interface OrderService extends IService<Order> {
    String createOrder(CreateOrderDTO createOrderDTO);
}
