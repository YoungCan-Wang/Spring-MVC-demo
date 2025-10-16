package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable int id) {
        UserResponse userResponse = userService.getUserById(id);
        return ApiResponse.success(userResponse);
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreateRequest createRequest) {
        UserResponse createdUser = userService.createUser(createRequest);
        return ApiResponse.success(createdUser);
    }
}