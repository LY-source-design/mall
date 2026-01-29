package pers.ly.mall.common.utils;


import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.exception.JwtParseException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {

    /**
     * 生成jwt令牌
     * @param secretKey 密钥
     * @param expiration 过期时间,单位毫秒
     * @param prefix 令牌前缀,这里是"Bearer "
     * @param claims 需要加入令牌的负载
     * @return 令牌字符串
     */
    public static String createToken(String secretKey, long expiration, String prefix, Map<String, Object> claims) {
        Date expirationTime = new Date(System.currentTimeMillis() + expiration);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return prefix + Jwts.builder().claims(claims)
                .signWith(key)
                .expiration(expirationTime)
                .compact();
    }

    /**
     * 解析令牌
     * @param token 令牌字符
     * @param prefix 前缀
     * @param secretKey 密钥
     * @return 返回令牌负载
     */
    public static Claims parseToken(String token, String prefix, String secretKey) {
        String tokenWithoutPrefix = getTokenWithoutPrefix(token, prefix);
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(tokenWithoutPrefix)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtParseException(ErrorConstant.TOKEN_EXPIRED);
        }
        return claims;
    }

    /**
     * 刷新令牌
     * @param token 令牌
     * @param expiration 原本的过期时间
     * @param prefix 前缀
     * @param secretKey 密钥
     * @return 新的令牌
     */
    public static String refreshToken(String token, long expiration, String prefix, String secretKey) {
        Claims claims = parseToken(token, prefix, secretKey);
        return createToken(secretKey, expiration, prefix, claims);
    }


    /**
     * 获取过期时间
     * @param token 令牌
     * @param prefix 前缀
     * @param secretKey 密钥
     * @return 日期
     */
    public static Date getExpiration(String token, String prefix, String secretKey) {
        Claims claims = parseToken(token, prefix, secretKey);
        return claims.getExpiration();
    }

    /**
     * 获取没有Bearer头的token
     * @param token 带头令牌
     * @param prefix 前缀
     * @return 没有Bearer头的令牌
     */
    private static String getTokenWithoutPrefix(String token, String prefix) {
        if(token ==null|| StrUtil.isBlank(token)){
            throw new JwtParseException(ErrorConstant.TOKE_IS_EMPTY);
        }
        if(!token.startsWith(prefix)){
            throw new JwtParseException(ErrorConstant.TOKEN_NOT_VALID);
        }
        return token.substring(prefix.length());
    }

}
