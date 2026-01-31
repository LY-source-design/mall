package pers.ly.mall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchSimpleSeckillGoodVO {
    private Long seckillGoodId;
    private Long goodId;
    private String goodName;
    private String goodImage;
    private BigDecimal price;
}
