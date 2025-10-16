package com.example.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 注解触发的参数校验异常
     *
     * @param ex a MethodArgumentNotValidException instance
     * @return a structured error response with field-specific messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
    }

    /**
     * 处理因参数类型不匹配导致的异常
     * @param ex a MethodArgumentTypeMismatchException instance
     * @return a structured error response
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleTypeMismatchException(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String error = String.format("参数 '%s' 的值 '%s' 类型不正确，期望的类型是 '%s'。", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), error, null);
    }

    /**
     * 兜底处理所有其他未被捕获的异常
     * @param ex an Exception instance
     * @return a generic 500 error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleAllExceptions(Exception ex) {
        // In a real project, you should log the exception ex.printStackTrace();
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误: " + ex.getClass().getSimpleName(), null);
    }
}