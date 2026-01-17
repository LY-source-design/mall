package pers.ly.mall.good.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchGoodVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long sales;
    private String image;
}
