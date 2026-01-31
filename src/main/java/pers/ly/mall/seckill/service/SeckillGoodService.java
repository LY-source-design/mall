package pers.ly.mall.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.SeckillGood;
import pers.ly.mall.seckill.vo.OrderNumberVO;
import pers.ly.mall.seckill.vo.SearchSeckillGoodVO;
import pers.ly.mall.seckill.vo.SearchSimpleSeckillGoodVO;

import java.util.List;

public interface SeckillGoodService extends IService<SeckillGood> {
    void addSeckillGood(SeckillGood seckillGood);

    List<SearchSimpleSeckillGoodVO> listSeckillGoodInDay();

    void seckill(Long seckillGoodId);

    OrderNumberVO paySeckill(Long seckillGoodId);

    SearchSeckillGoodVO searchSeckillGoodById(Long seckillGoodId);
}
