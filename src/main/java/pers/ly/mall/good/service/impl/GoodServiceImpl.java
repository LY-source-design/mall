package pers.ly.mall.good.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.constant.OssConstant;
import pers.ly.mall.common.entity.Category;
import pers.ly.mall.common.entity.Good;
import pers.ly.mall.common.entity.GoodCategory;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.common.exception.GoodException;
import pers.ly.mall.common.exception.OssUploadException;
import pers.ly.mall.common.utils.AliyunOssUtils;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.AddGoodDTO;
import pers.ly.mall.good.dto.SearchGoodDTO;
import pers.ly.mall.good.dto.UpdateGoodDTO;
import pers.ly.mall.good.mapper.CategoryMapper;
import pers.ly.mall.good.mapper.GoodCategoryMapper;
import pers.ly.mall.good.mapper.GoodMapper;
import pers.ly.mall.good.service.EsService;
import pers.ly.mall.good.service.GoodService;
import pers.ly.mall.good.vo.UpdateGoodStatusVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {
    private final GoodMapper goodMapper;
    private final CategoryMapper categoryMapper;
    private final GoodCategoryMapper goodCategoryMapper;
    private final EsService esService;
    private final AliyunOssUtils aliyunOssUtils;

    GoodServiceImpl(GoodMapper goodMapper, CategoryMapper categoryMapper, GoodCategoryMapper goodCategoryMapper,  EsService esService,  AliyunOssUtils aliyunOssUtils) {
        this.goodMapper = goodMapper;
        this.categoryMapper = categoryMapper;
        this.goodCategoryMapper = goodCategoryMapper;
        this.esService = esService;
        this.aliyunOssUtils = aliyunOssUtils;
    }

    /**
     * 添加商品
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

    @Override
    public String updateGoodImage(MultipartFile goodImage) {
        String originalFilename = goodImage.getOriginalFilename();
        if (originalFilename != null && (originalFilename.endsWith("jpg") ||
                originalFilename.endsWith("jpeg") ||
                originalFilename.endsWith("png") ||
                originalFilename.endsWith("gif"))) {
            return aliyunOssUtils.upload(OssConstant.GOOD_IMAGE_PATH, goodImage);
        }
        else {
            throw new OssUploadException(ErrorConstant.FILE_IS_VALID);
        }
    }

    @Transactional
    @Override
    public void updateGoodById(UpdateGoodDTO updateGoodDTO) {
        Long id = updateGoodDTO.getId();
        if (id == null) {
            throw new GoodException(ErrorConstant.ID_IS_VALID);
        }
        //更新mysql
        //更新mysql商品部分
        LambdaUpdateWrapper<Good> updateGoodWrapper = new LambdaUpdateWrapper<>();
        if(StrUtil.isNotBlank(updateGoodDTO.getName())) {
            updateGoodWrapper.set(Good::getName, updateGoodDTO.getName());
        }
        if(StrUtil.isNotBlank(updateGoodDTO.getContent())){
            updateGoodWrapper.set(Good::getContent, updateGoodDTO.getContent());
        }
        if(StrUtil.isNotBlank(updateGoodDTO.getImage())) {
            updateGoodWrapper.set(Good::getImage, updateGoodDTO.getImage());
        }
        if (updateGoodDTO.getPrice() != null) {
            updateGoodWrapper.set(Good::getPrice, updateGoodDTO.getPrice());
        }
        if (updateGoodDTO.getIsOnSale()!=null) {
            updateGoodWrapper.set(Good::getIsOnSale, updateGoodDTO.getIsOnSale());
        }
        updateGoodWrapper.set(Good::getUpdateTime, LocalDateTime.now());
        updateGoodWrapper.eq(Good::getId, id);
        update(updateGoodWrapper);
        //更新mysql分类部分
        List<Category> categoryList = null;
        if(updateGoodDTO.getCategoryIds() != null &&  !updateGoodDTO.getCategoryIds().isEmpty()) {
            List<Long> categoryIds = updateGoodDTO.getCategoryIds();
            LambdaQueryWrapper<Category> queryCategoryWrapper = new LambdaQueryWrapper<>();
            queryCategoryWrapper
                    .select(Category::getId, Category::getName)
                    .in(Category::getId, categoryIds);
            categoryList = categoryMapper.selectList(queryCategoryWrapper);
            //删除原来的
            goodCategoryMapper.delete(new LambdaQueryWrapper<GoodCategory>().eq(GoodCategory::getGoodId, id));
            //添加目标的
            goodCategoryMapper.insert(categoryList.stream().map(category -> {
                GoodCategory goodCategory = new GoodCategory();
                goodCategory.setCategoryId(category.getId());
                goodCategory.setGoodId(id);
                return goodCategory;
            }).toList());
        }
        //es更新
        esService.updateGoodById(updateGoodDTO, categoryList);
    }

    @Override
    public UpdateGoodStatusVO updateGoodStatus(Long id) {
        //查询当前状态
        Good good = query().select("is_on_sale").eq("id", id).one();
        if (good == null) {
            throw new GoodException(ErrorConstant.ID_IS_VALID);
        }
        //获取需要跟新到的状态
        Short isOnSale = Objects.equals(good.getIsOnSale(), Good.ON_SALE) ? Good.NOT_ON_SALE : Good.ON_SALE;
        //更新这里为了方便就调用之前的更新接口
        UpdateGoodDTO updateGoodDTO = new UpdateGoodDTO();
        updateGoodDTO.setId(id);
        updateGoodDTO.setIsOnSale(isOnSale);
        //获取代理对象(因为自调事务注解不生效)
        GoodService proxy = (GoodService) AopContext.currentProxy();
        proxy.updateGoodById(updateGoodDTO);
        //构造返回结果并返回
        UpdateGoodStatusVO updateGoodStatusVO = new UpdateGoodStatusVO();
        updateGoodStatusVO.setIsOnSale(isOnSale);
        return updateGoodStatusVO;
    }

}
