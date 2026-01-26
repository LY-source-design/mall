package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


//id bigint unsigned primary key auto_increment comment "订单id",
//order_num varchar(30) not null unique comment "订单号",
//user_id bigint unsigned not null comment "用户id",
//car_id bigint unsigned not null comment "购物车id",
//price decimal(10, 2) not null comment "总价",
//status tinyint not null comment "状态" #待支付0, 待送达1, 已送达2, 已取消3
//create_time datetime not null comment "创建时间",
//update_time datetime not null comment "更新时间"
@Data
@TableName("tb_order")
public class Order {
    //status状态的常量
    public final static Short NOT_PAY = 0;
    public final static Short WAIT_TO_REACH = 1;
    public final static Short ALREADY_REACH = 2;
    public final static Short CANCEL = 3;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long carId;
    private BigDecimal price;
    private Short status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
