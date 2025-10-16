package com.example.demo.mapper;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a UserCreateRequest DTO to a User entity.
     *
     * @param request The source DTO.
     * @return The mapped User entity.
     */
    User toEntity(UserCreateRequest request);

    /**
     * Maps a User entity to a UserResponse DTO.
     *
     * @param user The source entity.
     * @return The mapped UserResponse DTO.
     */
    UserResponse toResponse(User user);
}
