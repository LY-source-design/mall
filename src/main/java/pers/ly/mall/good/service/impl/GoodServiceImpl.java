package pers.ly.mall.good.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.good.mapper.GoodMapper;
import pers.ly.mall.good.service.GoodService;

@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {
}
