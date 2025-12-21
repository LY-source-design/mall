package pers.ly.mall.user.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Long userid;
    private String accessToken;
    private String refreshToken;
}
