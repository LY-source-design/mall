package pers.ly.mall.good.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.ly.mall.common.annotation.AdminApi;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.service.GoodService;

@RestController
@RequestMapping("/good")
@Tag(name = "商品管理", description = "商品相关的接口")
public class GoodController {
    private final GoodService goodService;

    GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    /**
     * 上架商品
     * @param addGoodDTO 商品信息
     * @return 返回成功信号
     */
    @AdminApi
    @PostMapping
    @Operation(summary = "上架商品", description = "上架商品")
    public Result<String> addGood(@RequestBody AddGoodDTO addGoodDTO){
        goodService.addGood(addGoodDTO);
        return Result.success("上架成功");
    }
}
