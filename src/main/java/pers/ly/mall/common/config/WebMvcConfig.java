package pers.ly.mall.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import pers.ly.mall.common.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    LoginInterceptor loginInterceptor;

    WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    /**
     * 注册登录拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/me")
                .addPathPatterns("/user/lock")
                .addPathPatterns("/good/**")
                .excludePathPatterns("/good/list")
                .addPathPatterns("/shoppingCar/**")
                .addPathPatterns("/order/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对项目所有接口生效
                // 核心替换：用allowedOriginPatterns，写前端具体源地址（开发环境）
                .allowedOriginPatterns("http://localhost:5173")
                .allowCredentials(true) // 保留登录必备的凭证支持
                .allowedHeaders("*") // 允许所有请求头（适配前端自定义头如Token）
                // 允许所有常规请求方法，必须包含OPTIONS（预检请求）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600); // 预检请求缓存1小时，优化性能
    }
}
