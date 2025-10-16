package com.example.demo.dto;

/**
 * DTO for returning user information to the client.
 * Using a record for immutability and conciseness.
 */
public record UserResponse(Integer id, String name, String role) {
}
