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
    //TODO: 添加标记状态的字段,让没生辰订单的不超过1个
    //TODO: 利用redis处理购物车
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
