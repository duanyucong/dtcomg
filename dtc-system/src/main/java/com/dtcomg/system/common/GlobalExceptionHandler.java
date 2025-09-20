package com.dtcomg.system.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<?> handleServiceException(ServiceException e) {
        logger.error(e.getMessage(), e);
        return ApiResult.error(e.getCode() != null ? e.getCode() : HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleRuntimeException(RuntimeException e) {
        logger.error("发生未知异常", e);
        return ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<?> handleAccessDeniedException(AccessDeniedException e) {
        logger.error("权限校验失败", e);
        return ApiResult.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    /**
     * 用户名或密码错误异常
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<?> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException e) {
        logger.error("用户名或密码错误", e);
        return ApiResult.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
    }

    // You can add more exception handlers here for specific exceptions like MethodArgumentNotValidException, etc.
}
