package pers.ly.mall.good.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.ly.mall.common.entity.Category;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.good.service.CategoryService;

import java.util.List;

@Tag(name = "分类管理", description = "分类管理相关接口")
@RequestMapping("/category")
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询所有分类
     * @return 分类信息
     */
    @Operation(summary = "查询所有分类", description = "查询所有分类")
    @GetMapping("/all")
    public Result<List<Category>> findAll(){
        List<Category> result = categoryService.list();
        return Result.success(result);
    }
}
