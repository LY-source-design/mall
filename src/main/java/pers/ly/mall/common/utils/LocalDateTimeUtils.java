package pers.ly.mall.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeUtils {
    public static long localDateTimeToMs(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0L;
        }
        // 关键：LocalDateTime需绑定时区（ZoneId）才能转时间戳，国内用ZoneId.of("Asia/Shanghai")
        return localDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(); // 最终获取毫秒时间戳（等价于Date的getTime()）
    }
}
