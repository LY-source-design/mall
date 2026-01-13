package pers.ly.mall.good.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.good.dto.AddGoodDTO;

public interface GoodService extends IService<Good> {
    void addGood(AddGoodDTO addGoodDTO);
}
