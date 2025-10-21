package com.example.demo.dto;

/**
 * A DTO to hold the result of a JOIN query between Post and User.
 */
public record PostAuthorDto(
        String postTitle,
        String authorName
) {
}
