package pers.ly.mall.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.annotation.AdminApi;
import pers.ly.mall.common.context.CurrentContext;
import pers.ly.mall.common.entity.User;
import pers.ly.mall.common.entity.UserInfo;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.user.dto.UserLoginDTO;
import pers.ly.mall.user.service.UserInfoService;
import pers.ly.mall.user.service.UserService;
import pers.ly.mall.user.vo.UserLoginVO;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理的相关接口")
public class UserController {

    private final UserService userService;
    private final UserInfoService userInfoService;

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
    @Operation(summary = "注册用户", description = "用于给用户注册账号")
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        log.info("开始注册用户");
        userService.register(user);
        return Result.success("账号注册成功");
    }

    /**
     * 登录
     * @param userLoginDTO 用户信息
     * @return 返回双令牌
     */
    @Operation(summary = "登录", description = "用于进行登录操作")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO result = userService.login(userLoginDTO);
        return Result.success(result);
    }

    /**
     * 获取用户的个人信息
     * @return 返回个人信息
     */
    @Operation(summary = "获取个人信息", description = "获取自己的个人信息")
    @GetMapping("/me")
    public Result<UserInfo> me() {
        Long userId = CurrentContext.getUserId();
        UserInfo result = userInfoService.getById(userId);
        return Result.success(result);
    }

    /**
     * 上传头像文件
     * @param avatar 头像文件
     * @return 文件路径
     */
    @Operation(summary = "上传头像文件", description = "上传头像")
    @PostMapping("/avatar/upload")
    public Result<String> updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        String path = userInfoService.updateAvatar(avatar);
        return Result.success(path);
    }

    /**
     * 修改个人信息
     * @Param userInfo 修改的用户信息
     * @return 返回成功信号
     */
    @Operation(summary = "修改个人信息", description = "修改个人信息")
    @PostMapping("/me")
    public Result<String> updateInfo(@RequestBody UserInfo userInfo) {
        Long userId = CurrentContext.getUserId();
        userInfo.setId(userId);
        boolean flag = userInfoService.updateById(userInfo);
        return flag ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 锁定账号
     * @param id 要锁定的账号id
     * @return 返回成功信号
     */
    @AdminApi
    @Operation(summary = "锁定账号", description = "锁定账号")
    @PostMapping("/lock")
    public Result<String> lockUser(@RequestParam(value = "id")Long id) {
        userService.lockUser(id);
        return Result.success("锁定成功");
    }
}
