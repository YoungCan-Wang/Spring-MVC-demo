package com.example.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice // 全局异常处理 + 自动返回 JSON
public class GlobalExceptionHandler {

    // 处理所有参数绑定或类型错误
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleValidationError(Exception e) {
        return ApiResponse.fail("参数校验失败: " + e.getMessage());
    }

    // 处理类型转换错误 (比如 int 接收到 "abc")
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleTypeError(Exception e) {
        return ApiResponse.fail("请求参数类型错误: " + e.getMessage());
    }

    // 兜底异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleOtherErrors(Exception e) {
        return ApiResponse.fail("服务器错误: " + e.getMessage());
    }
}
