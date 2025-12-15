package top.myzo.backend.utils;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object data;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(String message, Object data) {
        return new Result(Constants.SUCCESS, message, data);
    }

    public static Result success(Object data) {
        return new Result(Constants.SUCCESS, "操作成功", data);
    }

    public static Result success(String message) {
        return new Result(Constants.SUCCESS, message, null);
    }

    public static Result error(String message) {
        return new Result(Constants.ERROR, message, null);
    }

    public static Result unauthorized(String message) {
        return new Result(Constants.UNAUTHORIZED, message, null);
    }

    public static Result forbidden(String message) {
        return new Result(Constants.FORBIDDEN, message, null);
    }
}