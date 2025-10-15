package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
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
    public ApiResponse<User> getUser(@PathVariable int id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        return ApiResponse.success(userService.createUser(user));
    }
}
