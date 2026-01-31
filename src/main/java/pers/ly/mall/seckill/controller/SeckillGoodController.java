package pers.ly.mall.seckill.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pers.ly.mall.common.annotation.AdminApi;
import pers.ly.mall.common.entity.SeckillGood;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.seckill.service.SeckillGoodService;
import pers.ly.mall.seckill.vo.OrderNumberVO;
import pers.ly.mall.seckill.vo.SearchSeckillGoodVO;
import pers.ly.mall.seckill.vo.SearchSimpleSeckillGoodVO;

import java.util.List;

@Slf4j
@RequestMapping("/seckill")
@RestController
@Tag(name = "秒杀系统管理", description = "管理秒杀相关接口")
public class SeckillGoodController {
    private final SeckillGoodService seckillGoodService;
    public SeckillGoodController(SeckillGoodService seckillGoodService) {
        this.seckillGoodService = seckillGoodService;
    }

    /**
     * 添加秒杀商品(将已有的商品上添加秒杀商品)
     * @param seckillGood 秒杀商品信息
     * @return 返回添加结果
     */
    @AdminApi
    @Operation(summary = "添加秒杀商品(当天的发布)", description = "添加秒杀商品(当天的发布)")
    @PostMapping("/add")
    public Result<String> addSeckillGood(@RequestBody SeckillGood seckillGood) {
        seckillGoodService.addSeckillGood(seckillGood);
        return Result.success("成功添加为秒杀商品");
    }

    /**
     * 查询当天将要开始的秒杀活动
     * @return 返回活动列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询当天将要开始的秒杀活动", description = "查询当天将要开始的秒杀活动")
    public Result<List<SearchSimpleSeckillGoodVO>> listSeckillGoodInDay() {
        List<SearchSimpleSeckillGoodVO> result = seckillGoodService.listSeckillGoodInDay();
        return Result.success(result);
    }

    // ==========================================
    //          1./seckill请求抢资格(1.扣减库存(lua) 2.将用户加入zset(lua) 3.发送延迟消息)
    //          2.- /pay支付 剔除zset, 发起mq异步生成订单的请求,同时增加销量
    //            - 未支付 延迟消息到点还在zset里面,剔除zset,取消这次资格,回滚库存
    // ==========================================
    /**
     * 支付前抢购购买资格
     * @param seckillGoodId 商品id
     * @return 返回抢购信息
     */
    @Operation(summary = "抢购购买资格", description = "抢购购买资格")
    @PostMapping
    public Result<String> seckill(@RequestParam Long seckillGoodId) {
        seckillGoodService.seckill(seckillGoodId);
        return Result.success("抢购资格成功,请立刻支付");
    }

    /**
     * 支付
     * @param seckillGoodId 抢购商品id
     * @return 支付结果
     */
    @Operation(summary = "支付", description = "支付")
    @PostMapping("/pay")
    public Result<OrderNumberVO> paySeckill(@RequestParam Long seckillGoodId) {
        OrderNumberVO orderNumberVO = seckillGoodService.paySeckill(seckillGoodId);
        return Result.success(orderNumberVO);
    }

    /**
     * 查询商品的详细信息
     * @param seckillGoodId 抢购id
     * @return 抢购商品详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询商品的详细信息", description = "查询商品的详细信息")
    public Result<SearchSeckillGoodVO> searchSeckillGoodById(@PathVariable("id") Long seckillGoodId) {
        SearchSeckillGoodVO searchSeckillGoodVO = seckillGoodService.searchSeckillGoodById(seckillGoodId);
        return Result.success(searchSeckillGoodVO);
    }
}
