package pers.ly.mall.shoppingcar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.CarItem;
import pers.ly.mall.common.entity.ShoppingCar;
import pers.ly.mall.common.exception.DeleteGoodFromCarException;
import pers.ly.mall.shoppingcar.dto.UpdateShoppingCarDTO;
import pers.ly.mall.shoppingcar.mapper.CarItemMapper;
import pers.ly.mall.shoppingcar.mapper.ShoppingCarMapper;
import pers.ly.mall.shoppingcar.service.ShoppingCarService;

import java.time.LocalDateTime;

@Service
public class ShoppingCarServiceImpl extends ServiceImpl<ShoppingCarMapper, ShoppingCar> implements ShoppingCarService {
    private final CarItemMapper carItemMapper;
    public ShoppingCarServiceImpl(CarItemMapper carItemMapper) {
        this.carItemMapper = carItemMapper;
    }

    /**
     * 创建购物车
     */
    @Override
    public void addShoppingCar() {
        ShoppingCar shoppingCar = new ShoppingCar();
        shoppingCar.setUserId(CurrentContext.getUserId());
        shoppingCar.setCreateTime(LocalDateTime.now());
        shoppingCar.setUpdateTime(LocalDateTime.now());
        save(shoppingCar);
    }

    /**
     * 添加商品到购物车
     */
    @Override
    public void addGoodToShoppingCar(UpdateShoppingCarDTO updateShoppingCarDTO) {
        CarItem carItem = new CarItem();
        BeanUtils.copyProperties(updateShoppingCarDTO,carItem);
        carItemMapper.insert(carItem);
    }

    @Override
    public void deleteGoodFromShoppingCar(Long carId, Long goodId) {
        LambdaQueryWrapper<CarItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CarItem::getQuantity)
                .eq(CarItem::getGoodId, goodId)
                .eq(CarItem::getCarId, carId);
        Short quantity = carItemMapper.selectOne(queryWrapper).getQuantity();
        --quantity;
        if(quantity == 0){
            carItemMapper.delete(queryWrapper);
        }
        else if(quantity > 0){
            LambdaUpdateWrapper<CarItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(CarItem::getQuantity, quantity)
                    .eq(CarItem::getGoodId, goodId)
                    .eq(CarItem::getCarId, carId);
            carItemMapper.update(updateWrapper);
        }
        else {
            throw new DeleteGoodFromCarException(ErrorConstant.UNKNOWN_ERROR);
        }
    }
}
