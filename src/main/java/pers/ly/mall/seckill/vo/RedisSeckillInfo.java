package pers.ly.mall.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisSeckillInfo {
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
