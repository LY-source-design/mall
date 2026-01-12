package pers.ly.mall.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.order.dto.CreateOrderDTO;
import pers.ly.mall.order.service.OrderService;


@RestController
@RequestMapping("order")
@Tag(name = "订单管理", description = "订单相关的接口")
public class OrderController {
    private OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 生成订单
     * @param createOrderDTO 购物车id
     * @return 订单号
     */
    @Operation(summary = "生成订单", description = "生成订单")
    @PostMapping
    public Result<String> createOrder(CreateOrderDTO createOrderDTO) {
        String orderNum = orderService.createOrder(createOrderDTO);
        return Result.success(orderNum);
    }
}
