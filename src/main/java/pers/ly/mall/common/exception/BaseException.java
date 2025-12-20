package pers.ly.mall.common.exception;

public class BaseException extends RuntimeException {
    public  BaseException() {}
    public BaseException(String message) {
        super(message);
    }
}
