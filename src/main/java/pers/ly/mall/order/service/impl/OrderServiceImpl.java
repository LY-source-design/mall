package pers.ly.mall.order.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.constant.MqConstant;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.CarItem;
import pers.ly.mall.common.entity.DelayMessage;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.common.exception.DelayQueueException;
import pers.ly.mall.common.exception.ShoppingCarException;
import pers.ly.mall.common.utils.RedisIdGeneratorUtils;
import pers.ly.mall.good.service.GoodService;
import pers.ly.mall.order.vo.CreateOrderVO;
import pers.ly.mall.order.listener.DelayMessageHandle;
import pers.ly.mall.order.mapper.OrderMapper;
import pers.ly.mall.order.service.OrderService;
import pers.ly.mall.order.vo.GoodQuantityVO;
import pers.ly.mall.shoppingcar.dto.RedisCarItem;
import pers.ly.mall.shoppingcar.service.ShoppingCarService;
import pers.ly.mall.shoppingcar.vo.CheckOrderVO;
import pers.ly.mall.shoppingcar.vo.SearchGoodSimpleInfoVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private final RedisIdGeneratorUtils redisIdGeneratorUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final OrderMapper orderMapper;
    private final ShoppingCarService shoppingCarService;
    private final GoodService goodService;
    private final RabbitTemplate rabbitTemplate;

    public OrderServiceImpl(RedisIdGeneratorUtils redisIdGeneratorUtils,StringRedisTemplate stringRedisTemplate
            ,OrderMapper orderMapper,ShoppingCarService shoppingCarService
            ,GoodService goodService, RabbitTemplate rabbitTemplate) {
        this.redisIdGeneratorUtils = redisIdGeneratorUtils;
        this.stringRedisTemplate = stringRedisTemplate;
        this.orderMapper = orderMapper;
        this.shoppingCarService = shoppingCarService;
        this.goodService = goodService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 生成订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO createOrder() {
        Long userId = CurrentContext.getUserId();
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();

        BigDecimal price = new BigDecimal("0");
        //redis的key
        String key = "shoppingCar:" + userId.toString();
        Map<Long, Short> goodIdQuantity = getGoodIdQuantityMap(key);
        Set<Long> keys = goodIdQuantity.keySet();

        List<Good> goods = goodService.query().select("id", "price").in("id", keys).list();
        List<CarItem> carItems = new ArrayList<>();
        for (Good good : goods) {
            CarItem carItem = new CarItem();
            carItem.setGoodId(good.getId());
            carItem.setPrice(good.getPrice());
            carItem.setQuantity(goodIdQuantity.get(good.getId()));
            carItems.add(carItem);
            price = price.add(good.getPrice().multiply(BigDecimal.valueOf(goodIdQuantity.get(good.getId()))));
        }
        //生成订单号
        String orderNum = redisIdGeneratorUtils.nextId("order");
        //更新购物车状态(准确的来说是保存购物车)
        Long carId = shoppingCarService.saveShoppingCar(carItems);

        order.setUserId(userId);
        order.setOrderNumber(orderNum);
        order.setStatus(Order.NOT_PAY);
        order.setCarId(carId);
        order.setPrice(price);
        order.setCreateTime(now);
        order.setUpdateTime(now);

        save(order);

        //删除redis中购物车
        stringRedisTemplate.delete(key);
        //整合RabbitMQ实现延迟队列,1分钟未支付直接判定为取消(基于死信交换机)
        List<Long> delayTimeList = new ArrayList<>();
        Collections.addAll(delayTimeList, 10000L, 10000L, 10000L, 15000L, 15000L);
        DelayMessage<Long> longDelayMessage = new DelayMessage<>(order.getId(), delayTimeList);
        //发送订单号给交换机
        if (!longDelayMessage.hasNextDelay()) {
            throw new DelayQueueException(ErrorConstant.NO_FIRST_DELAY);
        }
        log.info("发送请求,订单id:" + order.getId());
        Long delay = longDelayMessage.removeNextDelay();
        rabbitTemplate.convertAndSend(MqConstant.ORDER_EXCHANGE, MqConstant.DELAY_ORDER_ROUTING_KEY,
                longDelayMessage, new DelayMessageHandle(delay));
        //创建返回信息
        CreateOrderVO createOrderVO = new CreateOrderVO();
        createOrderVO.setOrderId(order.getId());
        createOrderVO.setOrderNum(order.getOrderNumber());
        return createOrderVO;
    }

    /**
     * 核对订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckOrderVO checkOrder() {
        Long userId = CurrentContext.getUserId();
        String key = "shoppingCar:" + userId.toString();

        //查询goodId和quantity的map,用于后续计算价格和查询数据库
        Map<Long, Short> goodIdQuantity = getGoodIdQuantityMap(key);
        //计算总价格同时处理商品列表
        List<SearchGoodSimpleInfoVO> goodList = new ArrayList<>();
        //查询确切的商品信息,以防购物车信息已经被更新
        List<Good> goods = goodService.query().in("id", goodIdQuantity.keySet()).list();
        BigDecimal price = new BigDecimal("0");
        for (Good good : goods) {
            SearchGoodSimpleInfoVO searchGoodSimpleInfoVO = new SearchGoodSimpleInfoVO();
            searchGoodSimpleInfoVO.setGoodId(good.getId());
            searchGoodSimpleInfoVO.setGoodName(good.getName());
            searchGoodSimpleInfoVO.setPrice(good.getPrice());
            searchGoodSimpleInfoVO.setGoodImage(good.getImage());
            //这里商品数量需要和redis一致
            searchGoodSimpleInfoVO.setQuantity(goodIdQuantity.get(good.getId()));
            goodList.add(searchGoodSimpleInfoVO);
            price = price.add(good.getPrice().multiply(BigDecimal.valueOf(searchGoodSimpleInfoVO.getQuantity())));
        }

        CheckOrderVO checkOrderVO = new CheckOrderVO();
        checkOrderVO.setGoodList(goodList);
        checkOrderVO.setPrice(price);
        return checkOrderVO;
    }


    private Map<Long, Short> getGoodIdQuantityMap(String key) {
        //查询商品数目
        Map<Object, Object> goodInfoMap = stringRedisTemplate.opsForHash().entries(key);
        if (goodInfoMap.isEmpty()) {
            throw new ShoppingCarException(ErrorConstant.CAR_IS_EMPTY);
        }
        //查询goodId和quantity的map,用于后续计算价格和查询数据库
        HashMap<Long, Short> goodIdQuantity = new HashMap<>();
        goodInfoMap.forEach((k,v)->{
            //获取商品id
            String goodIdStr = (String)k;
            if(StrUtil.isEmpty(goodIdStr)){
                throw new ShoppingCarException(ErrorConstant.REDIS_FIND_ERROR);
            }
            Long goodId = Long.valueOf(goodIdStr);
            //获取商品数目
            String itemStr = (String)v;
            if(StrUtil.isEmpty(itemStr)){
                throw new ShoppingCarException(ErrorConstant.REDIS_FIND_ERROR);
            }
            RedisCarItem item = JSONUtil.toBean(itemStr, RedisCarItem.class);
            goodIdQuantity.put(goodId, item.getQuantity());
        });
        return goodIdQuantity;
    }

    /**
     * 支付订单
     * @param orderId 订单id
     */
    @Override
    public void pay(Long orderId) {
        //支付成功,更新数据库
        update().set("status", Order.WAIT_TO_REACH).eq("id", orderId).update();
        //发送消息给mq添加销量
        rabbitTemplate.convertAndSend(MqConstant.ORDER_EXCHANGE, MqConstant.SALES_ADD_KEY, orderId);
    }

    /**
     * 查询购买商品的数量和id
     * @param orderId 订单id
     * @return 查询结果
     */
    @Override
    public List<GoodQuantityVO> queryOrderInfoById(Long orderId) {
        return orderMapper.queryOrderInfoById(orderId);

    }

    /**
     * 添加mysql销量
     * @param goodIdWithQuantity 商品id和销量的关系
     */
    @Override
    public void addSales(List<GoodQuantityVO> goodIdWithQuantity) {
        if (goodIdWithQuantity == null || goodIdWithQuantity.isEmpty()) {
            return;
        }
        orderMapper.addSales(goodIdWithQuantity);
    }


}
