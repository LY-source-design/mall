package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;


//id bigint unsigned primary key auto_increment comment "订单id",
//user_id bigint unsigned not null comment "用户id",
//car_id bigint unsigned not null comment "购物车id",
//price decimal(10, 2) not null comment "总价",
//status tinyint not null comment "状态" #待支付0, 待送达1, 已送达2
@Data
@TableName("tb_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long goodId;
    private BigDecimal price;
    private Short status;
}
