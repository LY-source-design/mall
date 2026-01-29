package pers.ly.mall.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.order.vo.CreateOrderVO;
import pers.ly.mall.order.service.OrderService;
import pers.ly.mall.shoppingcar.vo.CheckOrderVO;

import java.util.List;


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

    /**
     * 查询历史订单
     * @param page 第几页
     * @param size 每页的大小
     * @return 返回分页结果
     */
    @Operation(summary = "查询历史订单", description = "查询历史订单")
    @GetMapping("/list")
    public Result<PageResult> searchOrders(@RequestParam Integer page, @RequestParam Integer size) {
        PageResult result = orderService.searchOrders(page, size);
        return Result.success(result);
    }

    /**
     * 取消订单
     * @param orderId 订单id
     * @return 取消结果
     */
    @Operation(summary = "取消订单", description = "在未支付前取消订单")
    @PostMapping("/cancel")
    public Result<String> cancelOrderWithoutPay(@RequestParam Long orderId) {
        orderService.cancelOrderWithoutPay(orderId);
        return Result.success("取消订单成功");
    }
}
