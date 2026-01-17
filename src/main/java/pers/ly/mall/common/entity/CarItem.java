package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

//id bigint unsigned primary key auto_increment comment "关联id",
//car_id bigint unsigned not null comment "购物车id",
//good_id bigint unsigned not null comment "商品id",
//quantity tinyint unsigned not null comment "购买数目",
//price decimal(10, 2) not null comment "购买价格"
@Data
@TableName("tb_car_item")
public class CarItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long carId;
    private Long goodId;
    private Short quantity;
    private BigDecimal price;
    private String goodName;
    private String goodImage;
}
