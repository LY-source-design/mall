package pers.ly.mall.shoppingcar.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchGoodSimpleInfoVO {
    Long goodId;
    String goodName;
    BigDecimal price;
    Short quantity;
    String goodImage;
}
