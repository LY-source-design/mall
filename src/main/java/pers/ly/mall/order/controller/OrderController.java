package pers.ly.mall.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.order.vo.CreateOrderVO;
import pers.ly.mall.order.service.OrderService;
import pers.ly.mall.shoppingcar.vo.CheckOrderVO;


@RestController
@RequestMapping("order")
@Tag(name = "订单管理", description = "订单相关的接口")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 生成订单前的核对操作
     * @return 返回数据库中准确的商品信息
     */
    @GetMapping
    @Operation(summary = "订单核对", description = "订单核对")
    public Result<CheckOrderVO> checkOrder() {
        CheckOrderVO result = orderService.checkOrder();
        return Result.success(result);
    }

    /**
     * 生成订单
     * @return 订单号
     */
    @Operation(summary = "生成订单", description = "生成订单")
    @PostMapping
    public Result<CreateOrderVO> createOrder() {
        CreateOrderVO result = orderService.createOrder();
        return Result.success(result);
    }

    /**
     * 支付订单
     * @param orderId 订单id
     * @return 返回支付信息
     */
    @Operation(summary = "支付订单", description = "支付订单")
    @PostMapping("/pay")
    public Result<String> pay(@RequestParam Long orderId) {
        orderService.pay(orderId);
        return Result.success("成功支付");
    }
}
