package pers.ly.mall.good.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("good")
@Tag(name = "商品管理", description = "商品相关的接口")
public class GoodController {

}
