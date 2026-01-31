package pers.ly.mall.shoppingcar.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.CarItem;
import pers.ly.mall.common.entity.ShoppingCar;
import pers.ly.mall.common.exception.ShoppingCarException;
import pers.ly.mall.shoppingcar.dto.RedisCarItem;
import pers.ly.mall.shoppingcar.dto.UpdateShoppingCarDTO;
import pers.ly.mall.shoppingcar.mapper.CarItemMapper;
import pers.ly.mall.shoppingcar.mapper.ShoppingCarMapper;
import pers.ly.mall.shoppingcar.service.ShoppingCarService;
import pers.ly.mall.shoppingcar.vo.SearchGoodSimpleInfoVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ShoppingCarServiceImpl extends ServiceImpl<ShoppingCarMapper, ShoppingCar> implements ShoppingCarService {
    private final CarItemMapper carItemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    public ShoppingCarServiceImpl(CarItemMapper carItemMapper,  StringRedisTemplate stringRedisTemplate) {
        this.carItemMapper = carItemMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private void refreshShoppingCarExpire(String key) {
        //设置/刷新过期时间
        stringRedisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    private RedisCarItem parseRedisHash(Long goodId, String key) {
        Object jsonObject = stringRedisTemplate.opsForHash().get(key, goodId.toString());
        if(jsonObject == null){
            if(!stringRedisTemplate.hasKey(key)){
                throw new ShoppingCarException(ErrorConstant.GOOD_IS_NOT_EXIST);
            }
            else {
                throw new ShoppingCarException(ErrorConstant.UNKNOWN_ERROR);
            }
        }
        String itemJson = jsonObject.toString();
        return JSONUtil.toBean(itemJson, RedisCarItem.class);
    }

    /**
     * 添加商品到购物车
     */
    @Override
    public void addGoodToShoppingCar(UpdateShoppingCarDTO updateShoppingCarDTO) {
        String key = "shoppingCar:" + CurrentContext.getUserId();
        Long goodId = updateShoppingCarDTO.getGoodId();
        RedisCarItem redisCarItem = null;
        if(stringRedisTemplate.opsForHash().hasKey(key, goodId.toString())){
            //在旧数据基础上+1
            redisCarItem = parseRedisHash(updateShoppingCarDTO.getGoodId(), key);
            redisCarItem.setQuantity((short) (redisCarItem.getQuantity() + 1));
        }
        else{
            //创建数据Map
            redisCarItem = new RedisCarItem();
            BeanUtils.copyProperties(updateShoppingCarDTO,redisCarItem);
            redisCarItem.setQuantity((short) 1);
        }
        //加入redis
        stringRedisTemplate.opsForHash().put(key, updateShoppingCarDTO.getGoodId().toString(), JSONUtil.toJsonStr(redisCarItem));
        //刷新过期时间
        refreshShoppingCarExpire(key);
    }



    /**
     * 从购物车移除商品(-1商品)
     * @param goodId 商品id
     */
    @Override
    public void reduceGoodFromShoppingCar(Long goodId) {
        String key = "shoppingCar:" + CurrentContext.getUserId();
        //获取商品数目
        RedisCarItem item = parseRedisHash(goodId, key);
        Short quantity = item.getQuantity();
        --quantity;
        if(quantity == 0){
            //为0删除商品
            stringRedisTemplate.opsForHash().delete(key, goodId.toString());
        }
        else if(quantity > 0){
            //更新商品数目
            item.setQuantity(quantity);
            stringRedisTemplate.opsForHash().put(key, goodId.toString(), JSONUtil.toJsonStr(item));
        }
        else {
            throw new ShoppingCarException(ErrorConstant.QUANTITY_ERROR);
        }
        //刷新过期时间
        refreshShoppingCarExpire(key);
    }

    /**
     * 查看购物车中的商品
     * @return 返回商品列表
     */
    @Override
    public List<SearchGoodSimpleInfoVO> searchGoodsInCar() {
       Long userId = CurrentContext.getUserId();
       String key = "shoppingCar:" + userId;
       Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
       List<SearchGoodSimpleInfoVO> searchGoodSimpleInfoVOList = new ArrayList<>();
       entries.forEach((k,v)->{
           //获取商品id
           String goodIdStr = (String)k;
           if(StrUtil.isEmpty(goodIdStr)){
               throw new ShoppingCarException(ErrorConstant.REDIS_FIND_ERROR);
           }
           //获取对应的redis中的商品信息
           Long goodId = Long.parseLong(goodIdStr);
           String itemStr = (String)v;
           if(StrUtil.isEmpty(itemStr)){
               throw new ShoppingCarException(ErrorConstant.REDIS_FIND_ERROR);
           }
           RedisCarItem item = JSONUtil.toBean(itemStr, RedisCarItem.class);
           SearchGoodSimpleInfoVO searchGoodSimpleInfoVO = new SearchGoodSimpleInfoVO();
           searchGoodSimpleInfoVO.setGoodId(goodId);
           BeanUtils.copyProperties(item, searchGoodSimpleInfoVO);
           searchGoodSimpleInfoVOList.add(searchGoodSimpleInfoVO);
       });
       return searchGoodSimpleInfoVOList;
    }

    @Transactional
    @Override
    public Long saveShoppingCar(List<CarItem> carItems, Long userId) {
        //保存购物车
        ShoppingCar shoppingCar = new ShoppingCar();
        shoppingCar.setUserId(userId);
        shoppingCar.setCreateTime(LocalDateTime.now());
        shoppingCar.setUpdateTime(LocalDateTime.now());
        save(shoppingCar);
        //保存购物车和商品关联
        carItems.forEach(carItem-> carItem.setCarId(shoppingCar.getId()));
        carItemMapper.insert(carItems);
        return shoppingCar.getId();
    }

    /**
     * 把商品移除购物车
     * @param goodId 商品id
     */
    @Override
    public void deleteGoodFromShoppingCar(Long goodId) {
        String key = "shoppingCar:" + CurrentContext.getUserId();
        stringRedisTemplate.opsForHash().delete(key, goodId.toString());
    }


}
