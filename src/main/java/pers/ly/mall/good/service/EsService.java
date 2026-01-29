package pers.ly.mall.good.service;

import pers.ly.mall.common.entity.Category;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.SearchGoodDTO;
import pers.ly.mall.good.dto.UpdateGoodDTO;
import pers.ly.mall.order.vo.GoodQuantityVO;

import java.util.List;

public interface EsService {
    void addGoodDoc(GoodDoc goodDoc);

    PageResult page(SearchGoodDTO searchGoodDTO);


    List<String> suggest(String query);

    void addSales(List<GoodQuantityVO> goodIdWithQuantity);

    void updateGoodById(UpdateGoodDTO updateGoodDTO, List<Category> categoryList);
}
