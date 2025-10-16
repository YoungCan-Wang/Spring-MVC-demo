package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for returning user information to the client.
 * Using a record for immutability and conciseness.
 */
@Schema(description = "用户信息响应体")
public record UserResponse(
        @Schema(description = "用户唯一ID", example = "1")
        Integer id,

        @Schema(description = "用户名", example = "Hanzhi")
        String name,

        @Schema(description = "用户角色", example = "Android Dev")
        String role
) {
}