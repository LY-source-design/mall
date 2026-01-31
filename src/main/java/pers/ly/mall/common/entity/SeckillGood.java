package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//id bigint primary key auto_increment comment "主键id",
//good_id bigint not null comment "关联的商品",
//price decimal(10,2) not null comment "秒杀价格",
//stock int not null comment "秒杀库存",
//begin_time datetime not null comment "开始时间",
//end_time datetime not null comment "结束时间"
@Data
@TableName("tb_seckill_good")
public class SeckillGood {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodId;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
