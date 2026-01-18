package pers.ly.mall.common.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

//id bigint unsigned primary key auto_increment comment "购物车id",
//user_id bigint unsigned not null comment "用户id",
//create_time datetime not null comment "创建时间",
//update_time datetime not null comment "更新时间"
@Data
@TableName("tb_shopping_car")
public class ShoppingCar {
    //status相关常量
    private final static Short SETTLED = 1; //已结算订单(无论订单状态如何)
    private final static Short UNSETTLED = 0; //正在使用的订单

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Short status; //标记购物车状态
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
