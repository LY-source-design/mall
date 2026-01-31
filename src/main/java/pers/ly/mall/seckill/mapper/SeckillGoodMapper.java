package pers.ly.mall.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.ly.mall.common.entity.SeckillGood;
import pers.ly.mall.seckill.vo.SearchSeckillGoodVO;
import pers.ly.mall.seckill.vo.SearchSimpleSeckillGoodVO;

import java.time.LocalDateTime;
import java.util.List;

public interface SeckillGoodMapper extends BaseMapper<SeckillGood> {
    List<SearchSimpleSeckillGoodVO> listSeckillGoodInDay(LocalDateTime begin, LocalDateTime end);

    SearchSeckillGoodVO searchSeckillGoodById(Long seckillGoodId);
}
