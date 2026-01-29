package pers.ly.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.order.vo.CreateOrderVO;
import pers.ly.mall.order.vo.GoodQuantityVO;
import pers.ly.mall.shoppingcar.vo.CheckOrderVO;

import java.util.List;

public interface OrderService extends IService<Order> {
    CreateOrderVO createOrder();

    CheckOrderVO checkOrder();

    void pay(Long orderId);

    List<GoodQuantityVO> queryOrderInfoById(Long orderId);

    void addSales(List<GoodQuantityVO> goodIdWithQuantity);

    PageResult searchOrders(Integer page, Integer size);

    void cancelOrderWithoutPay(Long orderId);
}
