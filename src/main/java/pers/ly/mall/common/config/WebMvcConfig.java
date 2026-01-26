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
                .addPathPatterns("/shoppingCar/**")
                .addPathPatterns("/order/**");
    }


}
