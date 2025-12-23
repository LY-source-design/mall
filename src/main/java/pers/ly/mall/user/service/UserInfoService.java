package pers.ly.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.entity.UserInfo;

public interface UserInfoService extends IService<UserInfo> {
    String updateAvatar(MultipartFile avatar);
}
