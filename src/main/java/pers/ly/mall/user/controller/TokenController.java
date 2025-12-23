package pers.ly.mall.user.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.common.exception.JwtParseException;
import pers.ly.mall.common.properties.JwtConfigProperties;
import pers.ly.mall.common.utils.JwtUtils;
import pers.ly.mall.user.dto.RefreshTokenDTO;
import pers.ly.mall.user.vo.RefreshTokenVO;

import java.util.Date;

@RestController
@RequestMapping("/token")
@Tag(name = "令牌管理", description = "令牌管理的相关接口")
public class TokenController {

    @Resource
    private JwtConfigProperties jwtConfigProperties;

    /**
     * 刷新访问令牌
     * @param refreshTokenDTO 刷新令牌
     */
    @Operation(summary = "更新令牌", description = "利用刷新令牌刷新访问令牌")
    @PostMapping("/refresh")
    public Result<RefreshTokenVO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            String token = refreshTokenDTO.getToken();
            Claims claims = JwtUtils.parseToken(token, jwtConfigProperties.getTokenPrefix(), jwtConfigProperties.getSecretKey());
            //这里是应为刷新令牌和登录令牌负载相同,所以直接用了刷新令牌的负载
            String accessToken = JwtUtils.createToken(jwtConfigProperties.getSecretKey(), jwtConfigProperties.getAccessExpiration(),
                    jwtConfigProperties.getTokenPrefix(), claims);
            //更具刷新令牌剩余有效期决定是否更新刷新令牌
            Date expiration = JwtUtils.getExpiration(token, jwtConfigProperties.getTokenPrefix(), jwtConfigProperties.getSecretKey());
            long restTime = expiration.getTime() - System.currentTimeMillis();
            if (restTime * 2 <= jwtConfigProperties.getRefreshExpiration()) {
                //只有有效期少于一般才跟新,避免更新频繁
                token = JwtUtils.refreshToken(token, jwtConfigProperties.getRefreshExpiration(),
                        jwtConfigProperties.getTokenPrefix(), jwtConfigProperties.getSecretKey());
            }
            RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
            refreshTokenVO.setAccessToken(accessToken);
            refreshTokenVO.setRefreshToken(token);
            return Result.success(refreshTokenVO);
        } catch (JwtParseException e) {
            e.printStackTrace();
            if(ErrorConstant.TOKEN_EXPIRED.equals(e.getMessage())) {
                //这里过期一定是刷新令牌过期了,但是要设置不同返回值,不然前端又要来刷新
                throw new JwtParseException(ErrorConstant.REFRESH_TOKEN_EXPIRE);
            }
            throw e;
        }
    }
}
