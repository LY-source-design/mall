package pers.ly.mall.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.ly.mall.common.annotation.AdminApi;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.common.exception.JwtParseException;
import pers.ly.mall.common.properties.JwtConfigProperties;
import pers.ly.mall.common.utils.JwtUtils;

import java.io.IOException;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtConfigProperties jwtConfigProperties;
    private final ObjectMapper mapper;

    LoginInterceptor(JwtConfigProperties jwtConfigProperties, ObjectMapper mapper) {
        this.jwtConfigProperties = jwtConfigProperties;
        this.mapper = mapper;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod handlerMethod)){
            //不是api调用直接放行
            return true;
        }

        log.info("服务器收到了请求:" + request.getRequestURI());

        String token = request.getHeader(jwtConfigProperties.getTokenHeader());
        try{
            Claims claims = JwtUtils.parseToken(token, jwtConfigProperties.getTokenPrefix(), jwtConfigProperties.getSecretKey());
            Long userId = ((Number) claims.get("userId")).longValue();
            String role = claims.get("role").toString();
            if(handlerMethod.getMethodAnnotation(AdminApi.class) != null) {
                //管理员接口
                if("user".equals(role)){
                    //不是管理员
                    handleError(response, ErrorConstant.PERMISSION_DENIED);
                    return false;
                }
            }
            CurrentContext.setUserId(userId);
            //解析成功,放行
            return true;
        } catch (JwtParseException e){
            //访问令牌无效,是否刷新令牌取决于e.getMessage()是否是令牌过期
            e.printStackTrace();
            String errorInfo = e.getMessage();
            if(ErrorConstant.TOKEN_EXPIRED.equals(e.getMessage())){
                errorInfo = ErrorConstant.DO_REFRESH_TOKEN;
            }
            handleError(response, errorInfo);
            return false;
        } catch (Exception e){
            //解析失败,拦截
            e.printStackTrace();
            handleError(response, ErrorConstant.UNKNOWN_ERROR);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentContext.clear();
    }

    private void handleError(HttpServletResponse response, String es) throws IOException {
        //这里千万不要用setError
        // 一旦用了这个,error的msg内容就不能自己写了,他会在返回给前端之前转发一个/error请求获取401页码的错误信息作为msg返回给前端
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        Result error = Result.error(es);
        mapper.writeValue(response.getWriter(), error);
    }
}
