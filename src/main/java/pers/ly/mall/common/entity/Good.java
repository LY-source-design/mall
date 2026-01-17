package pers.ly.mall.common.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//id bigint unsigned primary key auto_increment comment "商品id",
//name varchar(50) not null comment "商品名称",
//is_on_sale tinyint unsigned not null comment "是否上架",
//price decimal(10, 2) not null comment "商品价格",
//content varchar(100) not null comment "商品详情",
//create_time datetime not null comment "创建时间",
//update_time datetime not null comment "更新时间"
@Data
@TableName("tb_good")
public class Good {
    //isOnSale的常量
    public final static Short ON_SALE = 1;
    public final static Short NOT_ON_SALE = 0;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Short isOnSale;
    private BigDecimal price;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long sales;
    private String image;
}
