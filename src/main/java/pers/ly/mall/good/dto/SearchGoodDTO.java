package pers.ly.mall.good.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchGoodDTO {
    //orderRule常量
    public static final Short ASC = 0;
    public static final Short DESC = 1;

    private Integer from;
    private Integer size;
    private String search;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private List<Long> categoryIds;
    private String orderBy;
    private Short orderRule;
}
