package pers.ly.mall.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.ly.mall.common.entity.User;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.user.dto.UserLoginDTO;
import pers.ly.mall.user.service.UserInfoService;
import pers.ly.mall.user.service.UserService;
import pers.ly.mall.user.vo.UserLoginVO;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private UserInfoService userInfoService;

    //构造函数注入
    UserController(UserService userService, UserInfoService userInfoService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }

    /**
     * 注册账号
     * @param user 用户账号密码
     * @return 无返回值
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("开始注册用户");
        userService.register(user);
        return Result.success("账号注册成功");
    }

    /**
     * 登录
     * @param userLoginDTO 用户信息
     * @return 返回双令牌
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO result = userService.login(userLoginDTO);
        return Result.success(result);
    }
}
