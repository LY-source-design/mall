package pers.ly.mall.seckill.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.entity.DelayMessage;
import pers.ly.mall.common.exception.DelayQueueException;
import pers.ly.mall.common.handler.DelayMessageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SeckillMqListener {
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    public SeckillMqListener(StringRedisTemplate stringRedisTemplate,  RabbitTemplate rabbitTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    private static final DefaultRedisScript<Long> CANCEL_SCRIPT;
    static {
        CANCEL_SCRIPT = new DefaultRedisScript<>();
        CANCEL_SCRIPT.setResultType(Long.class);
        CANCEL_SCRIPT.setLocation(new ClassPathResource("lua/cancelSeckill.lua"));
    }

    @RabbitListener(queues = MqConstant.SECKILL_DELAY_QUEUE)
    public void handleDelayMessage(DelayMessage<Map<String,Object>> msg) {
        Map<String,Object> map = msg.getMessage();
        Long userId = Long.parseLong(map.get("userId").toString());
        Long seckillGoodId = Long.parseLong(map.get("seckillGoodId").toString());
        log.info("id为{}开始取消购买资格", userId);
        if (userId == null || seckillGoodId == null) {
            throw new DelayQueueException(ErrorConstant.UNKNOWN_ERROR);
        }
        String stockKey = "seckill:" + "stock:" + seckillGoodId;
        String purchasedSetKey = "seckill:" + "qualified:" + seckillGoodId;
        Boolean member = stringRedisTemplate.opsForSet().isMember(purchasedSetKey, userId.toString());
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(purchasedSetKey, userId.toString()))) {
            if(msg.hasNextDelay()) {
                Long delay = msg.removeNextDelay();
                rabbitTemplate.convertAndSend(MqConstant.SECKILL_DELAY_EXCHANGE, MqConstant.SECKILL_DELAY_KEY,
                        msg, new DelayMessageHandler(delay));
            }
            else {
                List<String> keys = new ArrayList<>();
                Collections.addAll(keys, stockKey, purchasedSetKey);
                Long execute = stringRedisTemplate.execute(CANCEL_SCRIPT, keys, userId.toString());
                if (execute == 1) {
                    log.info("id为{}用户取消资格", userId);
                }
                else {
                    log.info("id为{}用户已支付", userId);
                }
            }
        }
        else {
            log.info("id为{}用户已支付", userId);
        }
    }
}
