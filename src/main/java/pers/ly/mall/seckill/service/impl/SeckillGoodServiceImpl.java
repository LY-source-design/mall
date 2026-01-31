package pers.ly.mall.seckill.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.DelayMessage;
import pers.ly.mall.common.entity.SeckillGood;
import pers.ly.mall.common.exception.DelayQueueException;
import pers.ly.mall.common.exception.SeckillException;
import pers.ly.mall.common.handler.DelayMessageHandler;
import pers.ly.mall.common.utils.LocalDateTimeUtils;
import pers.ly.mall.common.utils.RedisIdGeneratorUtils;
import pers.ly.mall.good.service.GoodService;
import pers.ly.mall.seckill.dto.SeckillOrderGenerateDTO;
import pers.ly.mall.seckill.mapper.SeckillGoodMapper;
import pers.ly.mall.seckill.service.SeckillGoodService;
import pers.ly.mall.seckill.vo.OrderNumberVO;
import pers.ly.mall.seckill.vo.RedisSeckillInfo;
import pers.ly.mall.seckill.vo.SearchSeckillGoodVO;
import pers.ly.mall.seckill.vo.SearchSimpleSeckillGoodVO;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class SeckillGoodServiceImpl extends ServiceImpl<SeckillGoodMapper, SeckillGood> implements SeckillGoodService {
    private final GoodService goodService;
    private final SeckillGoodMapper seckillGoodMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisIdGeneratorUtils redisIdGeneratorUtils;

    public SeckillGoodServiceImpl(GoodService goodService, SeckillGoodMapper seckillGoodMapper,
                                  StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate,
                                  RedisIdGeneratorUtils redisIdGeneratorUtils) {
        this.goodService = goodService;
        this.seckillGoodMapper = seckillGoodMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.redisIdGeneratorUtils = redisIdGeneratorUtils;
    }

    //LUA脚本
    private static final DefaultRedisScript<Long> SECKILL_LUA_SCRIPT;
    private static final DefaultRedisScript<Long> PURCHASE_SCRIPT;
    static {
        SECKILL_LUA_SCRIPT = new DefaultRedisScript<>();
        SECKILL_LUA_SCRIPT.setResultType(Long.class);
        SECKILL_LUA_SCRIPT.setLocation(new ClassPathResource("lua/seckill.lua"));

        PURCHASE_SCRIPT = new DefaultRedisScript<>();
        PURCHASE_SCRIPT.setResultType(Long.class);
        PURCHASE_SCRIPT.setLocation(new ClassPathResource("lua/purchase.lua"));
    }

    /**
     * 添加秒杀商品(将已有的商品上添加秒杀商品)
     * @param seckillGood 秒杀商品信息
     */
    @Override
    public void addSeckillGood(SeckillGood seckillGood) {
        //获取商品id
        Long goodId = seckillGood.getGoodId();
        if(goodId==null){
            throw new SeckillException(ErrorConstant.ID_IS_VALID);
        }
        //查询商品是否存在
        Long count = goodService.query().eq("id", goodId).count();
        if(count == null || count <= 0){
            throw new SeckillException(ErrorConstant.GOOD_IS_NOT_EXIST);
        }
        //
        LocalDateTime beginTime = seckillGood.getBeginTime();
        LocalDateTime endTime = seckillGood.getEndTime();
        LocalDate now = LocalDate.now();
        if(beginTime.isAfter(endTime) || beginTime.isBefore(LocalDateTime.now())){
            throw new SeckillException(ErrorConstant.TIME_ERROR);
        }
        //进行添加逻辑
        save(seckillGood);
        Long seckillGoodId = seckillGood.getId();
        if(beginTime.isAfter(LocalDateTime.now()) && endTime.isBefore(LocalDateTime.of(now,LocalTime.MAX))){
            //发布抢购
            RedisSeckillInfo redisSeckillInfo = new RedisSeckillInfo();
            redisSeckillInfo.setBeginTime(beginTime);
            redisSeckillInfo.setEndTime(endTime);
            String infoJson = JSONUtil.toJsonStr(redisSeckillInfo);

            //构建key
            String infoKey = "seckill:" + "info:" + seckillGoodId;
            String stockKey = "seckill:" + "stock:" + seckillGoodId;
            String qualifiedSetKey = "seckill:" + "qualified:" + seckillGoodId;
            String purchasedSetKey = "seckill:" + "purchased:" + seckillGoodId;

            // 计算Redis键过期时间：秒杀结束后1小时过期，避免僵尸键（可根据业务调整）
            long endTimeMs = LocalDateTimeUtils.localDateTimeToMs(endTime);
            long expireMs = endTimeMs - System.currentTimeMillis() + 3600 * 1000L;
            // 防止过期时间为负数（如秒杀已结束），设置最小过期时间1分钟
            expireMs = Math.max(expireMs, 60 * 1000L);
            Duration expireDuration = Duration.ofMillis(expireMs);

            //防止脏key
            stringRedisTemplate.delete(infoKey);
            stringRedisTemplate.delete(stockKey);
            stringRedisTemplate.delete(purchasedSetKey);
            stringRedisTemplate.delete(qualifiedSetKey);

            //过期时间+redis对象创建
            stringRedisTemplate.opsForValue().set(infoKey, infoJson, expireDuration);
            stringRedisTemplate.opsForValue().set(stockKey, seckillGood.getStock().toString(), expireDuration);
            stringRedisTemplate.opsForSet().add(qualifiedSetKey, "0");
            stringRedisTemplate.opsForSet().add(purchasedSetKey, "0");
            stringRedisTemplate.expire(qualifiedSetKey, expireDuration);
            stringRedisTemplate.expire(purchasedSetKey, expireDuration);
        }
    }

    /**
     * 查询当天的有效抢购项目
     * @return 查询结果
     */
    @Override
    public List<SearchSimpleSeckillGoodVO> listSeckillGoodInDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
        return seckillGoodMapper.listSeckillGoodInDay(now, end);
    }

    /**
     * 支付前的购买
     * @param seckillGoodId 商品id
     */
    @Override
    public void seckill(Long seckillGoodId) {
        //获取用户id
        Long userId = CurrentContext.getUserId();
        log.info("id为{}开始获取购买资格", userId);
        //查询活动是否在时间范围内
        String infoKey = "seckill:" + "info:" + seckillGoodId;
        String infoJson = stringRedisTemplate.opsForValue().get(infoKey);
        if(StrUtil.isBlank(infoJson)){
            throw new SeckillException(ErrorConstant.GOOD_IS_NOT_EXIST);
        }
        RedisSeckillInfo info = JSONUtil.toBean(infoJson, RedisSeckillInfo.class);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(info.getBeginTime())) {
            throw new SeckillException(ErrorConstant.SECKILL_IS_NOT_BEGIN);
        }
        else if (now.isAfter(info.getEndTime())) {
            throw new SeckillException(ErrorConstant.SECKILL_IS_OVER);
        }
        //执行redis的lua脚本获取资格
        String stockKey = "seckill:" + "stock:" + seckillGoodId;
        String qualifiedSetKey = "seckill:" + "qualified:" + seckillGoodId;
        String purchasedSetKey = "seckill:" + "purchased:" + seckillGoodId;
        List<String> keys = new ArrayList<>();
        Collections.addAll(keys, stockKey, qualifiedSetKey, purchasedSetKey);
        Long result = stringRedisTemplate.execute(SECKILL_LUA_SCRIPT, keys, userId.toString());
        //处理异常
        if(result == null){
            throw new SeckillException(ErrorConstant.UNKNOWN_ERROR);
        }
        if (result == 0) {
            throw new SeckillException(ErrorConstant.GOOD_IS_NOT_EXIST); //商品不存在
        } else if (result == 1) {
            throw new SeckillException(ErrorConstant.HAVE_PURCHASED); //已经购买过了
        } else if (result == 2) {
            throw new SeckillException(ErrorConstant.HAVE_NOT_PAY); //说明插入失败,证明已经再队列里面了,还没支付
        }

        log.info("id为{}获取购买资格成功", userId);
        //发送mq延迟消息,防止别人抢了不买(1分钟不买直接回滚库存)
        List<Long> delayTimes = new ArrayList<>();
        Collections.addAll(delayTimes, 30000L, 30000L);
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("seckillGoodId", seckillGoodId);
        DelayMessage<Map<String,Object>> message = new DelayMessage<>(map, delayTimes);
        if (!message.hasNextDelay()) {
            throw new DelayQueueException(ErrorConstant.NO_FIRST_DELAY);
        }
        Long delay = message.removeNextDelay();
        log.info("为id为{}的用户发送延迟消息", userId);
        rabbitTemplate.convertAndSend(MqConstant.SECKILL_DELAY_EXCHANGE, MqConstant.SECKILL_DELAY_KEY,
                message, new DelayMessageHandler(delay));
    }

    /**
     * 支付
     * @param seckillGoodId 抢购商品id
     */
    @Override
    public OrderNumberVO paySeckill(Long seckillGoodId) {
        Long userId = CurrentContext.getUserId();
        String qualifiedSetKey = "seckill:" + "qualified:" + seckillGoodId;
        String purchasedSetKey = "seckill:" + "purchased:" + seckillGoodId;

        log.info("id为{}支付", userId);
        //执行redis的lua脚本
        List<String> keys = new ArrayList<>();
        Collections.addAll(keys, qualifiedSetKey, purchasedSetKey);
        Long execute = stringRedisTemplate.execute(PURCHASE_SCRIPT, keys, userId.toString());
        if(execute == 1){
            log.info("id为{}支付异常", userId);
            throw new SeckillException(ErrorConstant.PAY_TOO_LATE);
        }
        log.info("id为{}支付成功有效,开始发送异步请求创建订单", userId);
        //发送mq消息(异步生成订单,减少服务器压力)
        SeckillOrderGenerateDTO seckillOrderGenerateDTO = new SeckillOrderGenerateDTO();
        String orderNumber = redisIdGeneratorUtils.nextId("order");
        seckillOrderGenerateDTO.setOrderNumber(orderNumber);
        seckillOrderGenerateDTO.setSeckillId(seckillGoodId);
        seckillOrderGenerateDTO.setUserId(userId);
        rabbitTemplate.convertAndSend(MqConstant.ORDER_GENERATE_EXCHANGE, MqConstant.ORDER_GENERATE_KEY, seckillOrderGenerateDTO);
        OrderNumberVO ret = new OrderNumberVO();
        ret.setOrderNumber(orderNumber);
        return ret;
    }

    /**
     * 查询商品的详细信息
     * @param seckillGoodId 抢购id
     * @return 抢购商品详细信息
     */
    @Override
    public SearchSeckillGoodVO searchSeckillGoodById(Long seckillGoodId) {
        return seckillGoodMapper.searchSeckillGoodById(seckillGoodId);
    }
}
