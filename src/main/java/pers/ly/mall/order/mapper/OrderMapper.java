package pers.ly.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import pers.ly.mall.common.entity.Order;
import pers.ly.mall.order.vo.GoodQuantityVO;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    @Select("select tci.good_id, tci.quantity from tb_car_item tci " +
            "join tb_order tod on tci.car_id = tod.car_id where tod.id = #{orderId}")
    List<GoodQuantityVO> queryOrderInfoById(Long orderId);

    void addSales(List<GoodQuantityVO> goodIdWithQuantity);

    @Select("select id, order_number, user_id, car_id, price, status, create_time, update_time" +
            " from tb_order where user_id = #{userId} order by create_time desc limit #{offset}, #{size}")
    List<Order> pageByUserId(Long userId, Integer size, Integer offset);
}
