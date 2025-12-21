package pers.ly.mall.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mall.jwt")
@Data
public class JwtConfigProperties {
    private  String secretKey;
    //访问令牌的有效时间
    private Long accessExpiration;
    //刷新令牌的有效时间
    private Long refreshExpiration;
    private String tokenPrefix;
    private  String tokenHeader;
}
