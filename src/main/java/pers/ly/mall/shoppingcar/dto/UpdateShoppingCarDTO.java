package pers.ly.mall.shoppingcar.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateShoppingCarDTO {
    private Long carId;
    private Long goodId;
    private Short quantity;
    private BigDecimal price; //单价
    private String goodName;
    private String goodImage;
}
