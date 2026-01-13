package pers.ly.mall.good.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.entity.Category;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.GoodCategory;
import pers.ly.mall.common.exception.EsIOException;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.mapper.CategoryMapper;
import pers.ly.mall.good.mapper.GoodCategoryMapper;
import pers.ly.mall.good.mapper.GoodMapper;
import pers.ly.mall.good.service.GoodService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {
    private final GoodMapper goodMapper;
    private final CategoryMapper categoryMapper;
    private final GoodCategoryMapper goodCategoryMapper;
    private final RestHighLevelClient restHighLevelClient;

    GoodServiceImpl(GoodMapper goodMapper, CategoryMapper categoryMapper, GoodCategoryMapper goodCategoryMapper, RestHighLevelClient restHighLevelClient) {
        this.goodMapper = goodMapper;
        this.categoryMapper = categoryMapper;
        this.goodCategoryMapper = goodCategoryMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 上架商品
     * @param addGoodDTO 商品信息
     */
    @Transactional
    @Override
    public void addGood(AddGoodDTO addGoodDTO) {
        //处理商品数据库
        Good good = new Good();
        BeanUtils.copyProperties(addGoodDTO,good);
        good.setIsOnSale(Good.ON_SALE);
        good.setSales(0L);
        good.setCreateTime(LocalDateTime.now());
        good.setUpdateTime(LocalDateTime.now());
        save(good);
        //处理分类数据库
        List<GoodCategory> goodCategoryList = addGoodDTO.getCategoryIds()
                .stream().map(id -> {
                    GoodCategory goodCategory = new GoodCategory();
                    goodCategory.setCategoryId(id);
                    goodCategory.setGoodId(good.getId());
                    return goodCategory;
                })
                .toList();
        goodCategoryMapper.insert(goodCategoryList);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getName)
                .in(Category::getId, addGoodDTO.getCategoryIds());
        List<String> category = categoryMapper.selectList(queryWrapper)
                .stream().map(Category::getName).toList();
        //集成同步Es
        GoodDoc goodDoc = new GoodDoc(good, category);
        IndexRequest indexRequest = new IndexRequest("good").id(good.getId().toString());
        indexRequest.source(JSONUtil.toJsonStr(goodDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsIOException(ErrorConstant.ES_IO_ERROR);
        }
    }
}
