package pers.ly.mall.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    //配置redission客户端
    @Bean
    public RedissonClient redissonClient() {
        //创建配置
        Config config = new Config();
        //添加redis地址和密码等等
        config.useSingleServer().setAddress("redis://192.168.14.52:6379").setPassword("1038317691");
        //创建客户端
        return Redisson.create(config);
    }
}
