package pers.ly.mall.common.entity.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static<T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static Result success() {
        return new Result(200, "success");
    }

    public static Result<String> error(String msg) {
        return new Result<>(400, msg);
    }
}
