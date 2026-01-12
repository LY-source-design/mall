package pers.ly.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.ly.mall.common.entity.User;
import pers.ly.mall.user.dto.UserLoginDTO;
import pers.ly.mall.user.vo.UserLoginVO;

public interface UserService extends IService<User> {
    void register(User user);

    UserLoginVO login(UserLoginDTO userLoginDTO);

    void lockUser(Long id);
}
