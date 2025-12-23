package pers.ly.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_user_info")
public class UserInfo {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String nickname;
    private Short gender;
    private String bio;
    private String avatar;
}
