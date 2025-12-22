package pers.ly.mall.common.context;

public class CurrentContext {
    private static ThreadLocal<Integer> userIdLocal = new ThreadLocal<>();

    public static void setUserId(int userId) {
        userIdLocal.set(userId);
    }

    public static Integer getUserId() {
        return userIdLocal.get();
    }

    public static void clear() {
        userIdLocal.remove();
    }
}
