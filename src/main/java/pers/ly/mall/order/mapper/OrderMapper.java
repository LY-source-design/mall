package pers.ly.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.order.vo.GoodQuantityVO;

import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<Order> {

    @Select("select tci.good_id, tci.quantity from tb_car_item tci " +
            "join tb_order tod on tci.car_id = tod.car_id where tod.id = #{orderId}")
    List<GoodQuantityVO> queryOrderInfoById(Long orderId);

    void addSales(List<GoodQuantityVO> goodIdWithQuantity);
}
