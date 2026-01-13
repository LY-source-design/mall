package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_user")
public class User {
    //status常量
    public static final Short LOCK = 0;
    public static final Short UNLOCK = 1;
    //role常量
    public static final Short ADMIN = 0;
    public static final Short USER = 1;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Short status;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Short role;
}
