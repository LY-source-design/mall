package pers.ly.mall.good.service;

import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.SearchGoodDTO;

import java.util.List;

public interface EsService {
    void addGoodDoc(GoodDoc goodDoc);

    PageResult page(SearchGoodDTO searchGoodDTO);


    List<String> suggest(String query);
}
