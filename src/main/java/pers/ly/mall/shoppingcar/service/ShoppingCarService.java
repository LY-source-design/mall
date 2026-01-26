package pers.ly.mall.shoppingcar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.ShoppingCar;
import pers.ly.mall.shoppingcar.dto.UpdateShoppingCarDTO;
import pers.ly.mall.shoppingcar.vo.SearchGoodSimpleInfoVO;

import java.util.List;

public interface ShoppingCarService extends IService<ShoppingCar> {
    void addGoodToShoppingCar(UpdateShoppingCarDTO updateShoppingCarDTO);

    void deleteGoodFromShoppingCar(Long goodId);

    List<SearchGoodSimpleInfoVO> searchGoodsInCar();

    Long saveShoppingCar();
}
