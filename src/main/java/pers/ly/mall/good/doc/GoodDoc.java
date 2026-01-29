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
    private String image;
    private List<Long> categoryIds;
    private List<String> categories;
    private List<String> suggestion;

    public GoodDoc() {}

    public GoodDoc(Good good, List<Long> categoryIds, List<String> categories) {
        this.id = good.getId();
        this.name = good.getName();
        this.isOnSale = Objects.equals(good.getIsOnSale(), Good.ON_SALE);
        this.price = good.getPrice();
        this.content = good.getContent();
        this.sales = good.getSales();
        this.image = good.getImage();
        this.categoryIds = categoryIds;
        this.categories = categories;

        //合成suggestion
        initSuggest();
    }

    //用于初始化和更新再构建
    public void initSuggest() {
        suggestion = new ArrayList<>();
        suggestion.add(name);
        suggestion.addAll(categories);
    }
}
