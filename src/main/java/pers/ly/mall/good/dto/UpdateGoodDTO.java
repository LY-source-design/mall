package pers.ly.mall.good.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateGoodDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String content;
    private String image;
    private Short isOnSale;
    private List<Long> categoryIds;
}
