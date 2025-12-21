package pers.ly.mall.user.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.dao.DuplicateKeyException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.entity.User;
import pers.ly.mall.common.entity.UserInfo;
import pers.ly.mall.common.exception.LoginException;
import pers.ly.mall.common.exception.RegisterException;
import pers.ly.mall.common.properties.JwtConfigProperties;
import pers.ly.mall.common.utils.JwtUtils;
import pers.ly.mall.user.dto.UserLoginDTO;
import pers.ly.mall.user.mapper.UserInfoMapper;
import pers.ly.mall.user.mapper.UserMapper;
import pers.ly.mall.user.service.UserService;
import pers.ly.mall.user.vo.UserLoginVO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private UserMapper userMapper;
    private UserInfoMapper userInfoMapper;
    private PasswordEncoder passwordEncoder;
    private JwtConfigProperties jwtConfigProperties;

    UserServiceImpl(UserMapper userMapper, UserInfoMapper userInfoMapper,
                    PasswordEncoder passwordEncoder, JwtConfigProperties jwtConfigProperties) {
        this.userMapper = userMapper;
        this.userInfoMapper = userInfoMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfigProperties = jwtConfigProperties;
    }
    /**
     * 注册账号操作
     * @param user 用户信息
     */
    @Transactional
    @Override
    public void register(User user) {
        if(user.getUsername().isEmpty()){
            throw new RegisterException(ErrorConstant.USERNAME_IS_EMPTY);
        }
        if(user.getPassword().isEmpty()){
            throw new RegisterException(ErrorConstant.PASSWORD_IS_EMPTY);
        }

        String password = user.getPassword();
        user.setStatus((short) 1);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //注册账号,通过异常去重
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new RegisterException(ErrorConstant.USERNAME_EXIST);
        }

        //创建并添加个人信息(这里是默认值)
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname("user_" + UUID.randomUUID(true).toString().substring(0, 8));
        userInfo.setGender((short) 0);
        userInfo.setId(user.getId());
        userInfoMapper.insert(userInfo);
    }

    /**
     * 登录接口
     * @param userLoginDTO 用户登录信息
     * @return 双令牌
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //1. 查询账号密码是否正确
        // TODO 加密操作
        if(userLoginDTO.getUsername().isEmpty()){
            throw new LoginException(ErrorConstant.USERNAME_IS_EMPTY);
        }
        if(userLoginDTO.getPassword().isEmpty()){
            throw new LoginException(ErrorConstant.PASSWORD_IS_EMPTY);
        }
        //查询数据库,判断用户名是否存在以及密码是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
                .eq("username",userLoginDTO.getUsername());

        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            //查询错误
            throw new LoginException(ErrorConstant.USERNAME_ERROR);
        }
        if(!passwordEncoder.matches(userLoginDTO.getPassword(),user.getPassword())){
            //密码错误
            throw new LoginException(ErrorConstant.PASSWORD_ERROR);
        }
        if(user.getStatus() == 0) {
            //账号被锁定
            throw new LoginException(ErrorConstant.USER_STATUS_ERROR);
        }

        //正确查询
        Long userId = user.getId();
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        //2. 生成令牌
        //双令牌机制
        String accessToken = JwtUtils.createToken(jwtConfigProperties.getSecretKey()
                , jwtConfigProperties.getAccessExpiration(), jwtConfigProperties.getTokenPrefix(), map);
        String refreshToken = JwtUtils.createToken(jwtConfigProperties.getSecretKey()
                , jwtConfigProperties.getRefreshExpiration(), jwtConfigProperties.getTokenPrefix(), map);

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUserid(userId);
        userLoginVO.setAccessToken(accessToken);
        userLoginVO.setRefreshToken(refreshToken);

        return userLoginVO;
    }
}
