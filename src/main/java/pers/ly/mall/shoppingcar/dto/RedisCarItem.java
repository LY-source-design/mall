package pers.ly.mall.shoppingcar.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedisCarItem {
    private Short quantity;
    private BigDecimal price; //单价
    private String goodName;
    private String goodImage;
}
