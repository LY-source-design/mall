package pers.ly.mall.good.doc;

import lombok.Data;
import pers.ly.mall.common.entity.Good;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class GoodDoc {
    private Long id;
    private String name;
    private Boolean isOnSale;
    private BigDecimal price;
    private String content;
    private Long sales;
    private List<String> category;
    private List<String> suggestion;

    public GoodDoc(Good good, List<String> category) {
        this.id = good.getId();
        this.name = good.getName();
        this.isOnSale = Objects.equals(good.getIsOnSale(), Good.ON_SALE);
        this.price = good.getPrice();
        this.content = good.getContent();
        this.sales = good.getSales();
        this.category = category;

        //合成suggestion
        suggestion = new ArrayList<>();
        suggestion.add(name);
        suggestion.addAll(category);
    }


}
