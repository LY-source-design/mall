package pers.ly.mall.good.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddGoodDTO {
    private String name;
    private BigDecimal price;
    private String content;
    private List<Long> categoryIds;
}
