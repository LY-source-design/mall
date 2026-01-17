package pers.ly.mall.shoppingcar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.ShoppingCar;
import pers.ly.mall.shoppingcar.dto.UpdateShoppingCarDTO;

public interface ShoppingCarService extends IService<ShoppingCar> {
    void addShoppingCar();

    void addGoodToShoppingCar(UpdateShoppingCarDTO updateShoppingCarDTO);

    void deleteGoodFromShoppingCar(Long carId, Long goodId);
}
