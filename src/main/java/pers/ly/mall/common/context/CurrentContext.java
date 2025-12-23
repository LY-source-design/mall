package pers.ly.mall.common.context;

public class CurrentContext {
    private static ThreadLocal<Long> userIdLocal = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userIdLocal.set(userId);
    }

    public static Long getUserId() {
        return userIdLocal.get();
    }

    public static void clear() {
        userIdLocal.remove();
    }
}
