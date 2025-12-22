package pers.ly.mall.user.vo;

import lombok.Data;

@Data
public class RefreshTokenVO {
    String accessToken;
    String refreshToken;
}
