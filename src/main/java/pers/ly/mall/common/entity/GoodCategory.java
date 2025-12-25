package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//id bigint unsigned primary key auto_increment comment "关系id",
//good_id bigint unsigned not null comment "商品id",
//category_id bigint unsigned not null comment "种类id"
@Data
@TableName("tb_good_category")
public class GoodCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodId;
    private Long categoryId;
}
