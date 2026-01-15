package pers.ly.mall.common.constant;

public class ErrorConstant {

    //TODO: 后续转化成用户可以看懂的术语
    //登录校验的常量
    public static final String TOKE_IS_EMPTY = "token不能为空";
    public static final String TOKEN_NOT_VALID = "token格式不合法";
    public static final String TOKEN_EXPIRED = "token过期了";
    public static final String DO_REFRESH_TOKEN = "请求刷新令牌";
    public static final String REFRESH_TOKEN_EXPIRE = "刷新token过期了,请重新登录";
    public static final String PERMISSION_DENIED = "权限不足";

    //登录注册的常量
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String USERNAME_IS_EMPTY = "用户名不能为空";
    public static final String PASSWORD_IS_EMPTY = "密码不能为空";
    public static final String USERNAME_ERROR = "用户名错误";
    public static final String USER_STATUS_ERROR = "账号被锁定";
    public static final String PASSWORD_ERROR = "密码错误";

    //OSS文件上传
    public static final String NO_REACH_SERVER = "请求未到达服务器";
    public static final String REJECT_BY_SERVER = "请求被服务器拒绝";
    public static final String FILE_WRITE_ERROR = "文件读写异常";
    public static final String FILE_IS_VALID = "非法文件,文件只能是图片";

    //订单相关
    public static final String BEGIN_STAMP_VALID = "起始的订单时间不可用,请立即更新";
    public static final String ORDER_TOO_MUCK = "订单数量过多,服务即将崩溃";

    //ES相关
    public static final String ES_IO_ERROR = "ES通信异常";
    public static final String PAGE_OR_SIZE_ILLEGAL = "分页参数非法";

    //common
    public static final String UNKNOWN_ERROR = "未知错误";
}
