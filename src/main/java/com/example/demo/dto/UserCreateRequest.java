package com.example.demo.dto;

/**
 * DTO for receiving user creation requests from the client.
 * Using a record for immutability and conciseness.
 */
public record UserCreateRequest(String name, String role) {
}