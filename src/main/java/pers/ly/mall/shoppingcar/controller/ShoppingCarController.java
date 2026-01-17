package pers.ly.mall.shoppingcar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.shoppingcar.dto.UpdateShoppingCarDTO;
import pers.ly.mall.shoppingcar.service.ShoppingCarService;

@RestController
@RequestMapping("/shoppingCar")
@Tag(name = "购物车管理", description = "购物车相关的接口")
public class ShoppingCarController {
    private final ShoppingCarService shoppingCarService;

    public ShoppingCarController(ShoppingCarService shoppingCarService) {
        this.shoppingCarService = shoppingCarService;
    }

    /**
     * 创建购物车
     * @return 创建成功信息
     */
    @PostMapping("/create")
    @Operation(summary = "创建购物车", description = "创建购物车")
    public Result<String> addShoppingCar(){
        shoppingCarService.addShoppingCar();
        return Result.success("创建购物车成功");
    }

    /**
     * 添加商品到购物车
     * @return 返回添加结果
     */
    @PostMapping
    @Operation(summary = "添加商品到购物车", description = "添加商品到购物车")
    public Result<String> addGoodToShoppingCar(@RequestBody UpdateShoppingCarDTO updateShoppingCarDTO){
        shoppingCarService.addGoodToShoppingCar(updateShoppingCarDTO);
        return Result.success("添加成功");
    }

    /**
     * 从购物车移除商品(-1商品)
     * @param goodId 商品id
     * @return 返回删除结果
     */
    @DeleteMapping("/{carId}")
    @Operation(summary = "从购物车移除商品", description = "从购物车移除商品")
    public Result<String> deleteGoodFromShoppingCar(@PathVariable Long carId, @RequestParam Long goodId){
        shoppingCarService.deleteGoodFromShoppingCar(carId, goodId);
        return Result.success("成功移除");
    }

}
