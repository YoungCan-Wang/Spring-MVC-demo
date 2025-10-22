package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.dto.RoleCountDto;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    public UserResponse createUser(UserCreateRequest createRequest) {
        User user = userMapper.toEntity(createRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse updateUser(int id, UserUpdateRequest updateRequest) {
        // 1. 查找用户，如果不存在则抛出异常
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 更新用户信息
        existingUser.setName(updateRequest.name());
        existingUser.setRole(updateRequest.role());

        // 3. 保存更新后的用户
        User updatedUser = userRepository.save(existingUser);

        // 4. 转换为 Response DTO 并返回
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUserById(int id) {
        // 1. 检查用户是否存在，不存在则抛出异常
        if (!userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 2. 删除用户
        userRepository.deleteById(id);
    }

    public List<RoleCountDto> countUsersByRole() {
        return userRepository.countUsersByRole();
    }

    public List<RoleCountDto> countUsersByRoleHavingMinCount(long minCount) {
        return userRepository.countUsersByRoleHavingMinCount(minCount);
    }

    public List<UserResponse> findUsersByNameAndRole(String name, String role) {
        List<User> users = userRepository.findByNameAndRole(name, role);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
