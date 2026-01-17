package pers.ly.mall.good.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.dto.SearchGoodDTO;

import java.util.List;

public interface GoodService extends IService<Good> {
    void addGood(AddGoodDTO addGoodDTO);

    PageResult searchGoodList(SearchGoodDTO searchGoodDTO);

    List<String> suggest(String query);

    String updateGoodImage(MultipartFile goodImage);
}
