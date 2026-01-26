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
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
