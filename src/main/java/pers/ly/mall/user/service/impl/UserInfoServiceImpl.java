package pers.ly.mall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.ly.mall.common.entity.UserInfo;
import pers.ly.mall.user.mapper.UserInfoMapper;
import pers.ly.mall.user.service.UserInfoService;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}
