package top.myzo.backend.exception;

import top.myzo.backend.utils.Result;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public Result handleUnauthenticatedException(UnauthenticatedException e) {
        return Result.unauthorized("未登录或登录已过期");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Result handleUnauthorizedException(UnauthorizedException e) {
        return Result.unauthorized("权限不足");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error("服务器内部错误: " + e.getMessage());
    }
}