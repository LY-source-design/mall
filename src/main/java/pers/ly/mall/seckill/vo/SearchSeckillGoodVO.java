package pers.ly.mall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchSeckillGoodVO {
    private Long seckillGoodId;
    private Long goodId;
    private String goodName;
    private String goodImage;
    private Long sales;
    private BigDecimal originalPrice;
    private BigDecimal seckillPrice;
}
