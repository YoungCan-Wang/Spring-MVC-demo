package com.example.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice("com.example.demo.controller")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     *
     * @param ex a BusinessException instance
     * @return a ResponseEntity with a custom error code and dynamic HTTP status
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        logger.warn("业务异常: {}", errorCode.getMessage());
        ApiResponse<Object> body = ApiResponse.fail(errorCode.getCode(), errorCode.getMessage(), null);
        return new ResponseEntity<>(body, errorCode.getHttpStatus());
    }

    /**
     * 处理 @Valid 注解触发的参数校验异常
     * @param ex a MethodArgumentNotValidException instance
     * @return a ResponseEntity with a 400 status and structured error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("参数校验失败: {}", errors);
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiResponse<Map<String, String>> body = ApiResponse.fail(errorCode.getCode(), errorCode.getMessage(), errors);
        return new ResponseEntity<>(body, errorCode.getHttpStatus());
    }

    /**
     * 兜底处理所有其他未被捕获的异常
     * @param ex an Exception instance
     * @return a ResponseEntity with a 500 status and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        logger.error("发生未知异常", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> body = ApiResponse.fail(errorCode.getCode(), errorCode.getMessage(), null);
        return new ResponseEntity<>(body, errorCode.getHttpStatus());
    }
}
