package com.example.demo.common;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // --- 工厂方法 ---

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    /**
     * 失败响应，适用于业务失败，返回自定义消息和 500 状态码
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 失败响应（重载），适用于可指定状态码和错误数据的场景（如参数校验）
     */
    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }


    // --- getter / setter ---
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}