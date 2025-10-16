package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "根据ID查询用户", description = "传入用户ID，获取该用户的详细信息。")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功找到用户"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "当用户ID不存在时"),
    })
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @Parameter(description = "用户的唯一ID", required = true, example = "1")
            @PathVariable int id) {
        UserResponse userResponse = userService.getUserById(id);
        return ApiResponse.success(userResponse);
    }

    @Operation(summary = "注册新用户", description = "传入用户名和角色，创建一个新用户。ID由系统自动生成。")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数不合法（如name为空）"),
    })
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreateRequest createRequest) {
        UserResponse createdUser = userService.createUser(createRequest);
        return ApiResponse.success(createdUser);
    }
}