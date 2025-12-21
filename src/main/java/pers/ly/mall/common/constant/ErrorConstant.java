package pers.ly.mall.common.constant;

public class ErrorConstant {

    //TODO: 后续转化成用户可以看懂的术语
    //Jwt的常量
    public static final String TOKE_IS_EMPTY = "token不能为空";
    public static final String TOKEN_NOT_VALID = "token格式不合法";
    public static final String TOKEN_EXPIRED = "token过期了";

    //登录注册的常量
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String USERNAME_IS_EMPTY = "用户名不能为空";
    public static final String PASSWORD_IS_EMPTY = "密码不能为空";
    public static final String USERNAME_ERROR = "用户名错误";
    public static final String USER_STATUS_ERROR = "账号被锁定";
    public static final String PASSWORD_ERROR = "密码错误";
}
