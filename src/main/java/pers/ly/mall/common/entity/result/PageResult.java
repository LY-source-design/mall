package pers.ly.mall.common.entity.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long total;
    private List records;
}
