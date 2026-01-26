package pers.ly.mall.common.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.exception.OrderException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Redis的自增id作为1-32位
 * 采取now的毫秒值减去一个begin的毫秒值 作为订单的33-63位
 * 0作为第64位保证订单为正数
 */
@Component
public class RedisIdGeneratorUtils {
    private final StringRedisTemplate stringRedisTemplate;

    RedisIdGeneratorUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //总位数
    private final static int ALL_BIT_COUNT = 64;
    //Redis自增id的位数,也就是订单数量的位数
    private final static int NUM_BIT_COUNT = 32;
    //毫秒值的位数
    private final static int TIME_BIT_COUNT = 31;
    //起始时间
    private final static Long BEGIN_TIME =
            LocalDateTime.of(2025, 1, 1, 0, 0, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

    /**
     * 更具业务生成一个唯一id
     * @param prefix 业务前缀
     * @return 唯一id
     */
    public String nextId(String prefix) {
        Long now = System.currentTimeMillis();
        long stamp = (now - BEGIN_TIME) / 1000;

        long ret = 0L;
        ret |= stamp << NUM_BIT_COUNT;
        if (ret < 0) {
            //证明stamp超出了31位
            throw new OrderException(ErrorConstant.BEGIN_STAMP_VALID);
        }

        String key = prefix + ":" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);
        Long num = stringRedisTemplate.opsForValue().increment(key);
        if (num == null) {
            //理论上不会是空
            throw new OrderException(ErrorConstant.UNKNOWN_ERROR);
        }
        if(num >> 32 != 0) {
            //订单超出上线
            throw new OrderException(ErrorConstant.ORDER_TOO_MUCK);
        }
        ret |= num;

        return Long.toString(ret);
    }

}
