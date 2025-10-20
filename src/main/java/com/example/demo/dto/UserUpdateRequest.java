package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for receiving user update requests from the client.
 */
public record UserUpdateRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 2, max = 32, message = "Username must be between 2 and 32 characters")
        String name,

        @NotBlank(message = "Role cannot be blank")
        String role
) {
}
