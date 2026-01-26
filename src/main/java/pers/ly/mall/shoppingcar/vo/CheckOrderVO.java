package pers.ly.mall.shoppingcar.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckOrderVO {
    private BigDecimal price;
    private List<SearchGoodSimpleInfoVO> goodList;
}
