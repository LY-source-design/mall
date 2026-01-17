package pers.ly.mall.good.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.annotation.AdminApi;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.dto.SearchGoodDTO;
import pers.ly.mall.good.service.GoodService;

import java.util.List;

@RestController
@RequestMapping("/good")
@Tag(name = "商品管理", description = "商品相关的接口")
public class GoodController {
    private final GoodService goodService;

    GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    /**
     * 上传商品图片文件
     * @param goodImage 商品图片文件
     * @return 文件路径
     */
    @Operation(summary = "上传头像文件", description = "上传头像")
    @PostMapping("/avatar/upload")
    public Result<String> updateGoodImage(@RequestParam("goodImage") MultipartFile goodImage) {
        String path = goodService.updateGoodImage(goodImage);
        return Result.success(path);
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

    /**
     * 查询商品列表
     * @Param searchGoodDTO 查询条件
     * @return 分页查询结果
     */
    @PostMapping("/list")
    @Operation(summary = "查询商品列表", description = "查询商品列表")
    public Result<PageResult> searchGoodList(@RequestBody SearchGoodDTO searchGoodDTO){
        PageResult pageResult = goodService.searchGoodList(searchGoodDTO);
        return Result.success(pageResult);
    }

    /**
     * 自动补全
     * @param query 查询条件
     * @return 返回补全建议
     */
    @GetMapping("/suggest")
    @Operation(summary = "自动补全", description = "自动补全")
    public Result<List<String>> suggest(@RequestParam String query){
        List<String> result = goodService.suggest(query);
        return Result.success(result);
    }

    /**
     * 查询商品详细信息
     * @param id 商品id
     * @return 返回详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询商品详细信息", description = "查询商品详细信息")
    public Result<Good> searchGoodById(@PathVariable Long id){
        Good good = goodService.getById(id);
        return Result.success(good);
    }

    /**
     * 删除商品
     * @param id 商品id
     * @return 成功描述
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "删除商品")
    @AdminApi
    public Result<String> deleteGoodById(@PathVariable Long id){
        goodService.removeById(id);
        return Result.success("删除商品成功");
    }


}
