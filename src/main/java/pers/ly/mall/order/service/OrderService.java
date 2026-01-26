package pers.ly.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.order.VO.CreateOrderVO;
import pers.ly.mall.shoppingcar.vo.CheckOrderVO;

public interface OrderService extends IService<Order> {
    CreateOrderVO createOrder();

    CheckOrderVO checkOrder();
}
