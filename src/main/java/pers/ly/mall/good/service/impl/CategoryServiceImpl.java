package pers.ly.mall.good.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.entity.Category;
import pers.ly.mall.good.mapper.CategoryMapper;
import pers.ly.mall.good.service.CategoryService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
