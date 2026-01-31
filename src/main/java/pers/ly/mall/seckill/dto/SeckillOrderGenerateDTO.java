package pers.ly.mall.seckill.dto;

import lombok.Data;

@Data
public class SeckillOrderGenerateDTO {
    private String orderNumber;
    private Long userId;
    private Long seckillId;
}
