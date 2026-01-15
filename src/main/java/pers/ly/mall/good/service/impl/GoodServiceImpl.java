package pers.ly.mall.good.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.entity.Category;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.GoodCategory;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.dto.SearchGoodDTO;
import pers.ly.mall.good.mapper.CategoryMapper;
import pers.ly.mall.good.mapper.GoodCategoryMapper;
import pers.ly.mall.good.mapper.GoodMapper;
import pers.ly.mall.good.service.EsService;
import pers.ly.mall.good.service.GoodService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {
    private final GoodMapper goodMapper;
    private final CategoryMapper categoryMapper;
    private final GoodCategoryMapper goodCategoryMapper;
    private final EsService esService;

    GoodServiceImpl(GoodMapper goodMapper, CategoryMapper categoryMapper, GoodCategoryMapper goodCategoryMapper,  EsService esService) {
        this.goodMapper = goodMapper;
        this.categoryMapper = categoryMapper;
        this.goodCategoryMapper = goodCategoryMapper;
        this.esService = esService;
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
        List<String> categories = categoryMapper.selectList(queryWrapper)
                .stream().map(Category::getName).toList();
        //集成同步Es
        GoodDoc goodDoc = new GoodDoc(good, addGoodDTO.getCategoryIds(), categories);
        esService.addGoodDoc(goodDoc);
    }

    /**
     * 查询商品列表
     * @Param searchGoodDTO 查询条件
     * @return 分页查询结果
     */
    @Override
    public PageResult searchGoodList(SearchGoodDTO searchGoodDTO) {
        return esService.page(searchGoodDTO);
    }

    /**
     * 自动补全
     * @param query 查询条件
     * @return 返回补全建议
     */
    @Override
    public List<String> suggest(String query) {
        return esService.suggest(query);
    }
}
