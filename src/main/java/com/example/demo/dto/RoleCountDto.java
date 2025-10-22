package com.example.demo.dto;

/**
 * DTO for holding the result of a GROUP BY query on user roles.
 */
public record RoleCountDto(
        String role,
        long userCount // The result of COUNT() is of type Long
) {
}
