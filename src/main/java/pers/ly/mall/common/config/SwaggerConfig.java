package pers.ly.mall.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 对于swagger文档的配置
     * 不对于DTO和VO进行注解配置,直接见名知意就好了
     * @return 返回配置的bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("mall商城")
                        .version("1.0")
                        .description("这是springboot3集成swagger3的接口文档")
                        .contact(new Contact()
                                .name("ly"))
                );
    }
}
