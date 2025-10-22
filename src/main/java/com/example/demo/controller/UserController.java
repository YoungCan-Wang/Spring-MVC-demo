package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.RoleCountDto;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "根据ID查询用户", description = "传入用户ID，获取该用户的详细信息。")
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功找到用户"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "当用户ID不存在时"),})

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@Parameter(description = "用户的唯一ID", required = true, example = "1") @PathVariable int id) {
        UserResponse userResponse = userService.getUserById(id);
        return ApiResponse.success(userResponse);
    }

    @Operation(summary = "注册新用户", description = "传入用户名和角色，创建一个新用户。ID由系统自动生成。")
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户创建成功"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数不合法（如name为空）"),})

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreateRequest createRequest) {
        UserResponse createdUser = userService.createUser(createRequest);
        return ApiResponse.success(createdUser);
    }

    @Operation(summary = "更新用户信息", description = "传入用户ID和新的用户信息，更新该用户。")
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户更新成功"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "当用户ID不存在时"),})

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateUser(id, updateRequest);
        return ApiResponse.success(updatedUser);
    }

    @Operation(summary = "删除用户", description = "传入用户ID，删除该用户。")
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户删除成功"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "当用户ID不存在时"),})

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "统计各角色用户数量", description = "统计每个角色下的用户数量。")
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "统计成功"),})

    @GetMapping("/countByRole")
    public ApiResponse<List<RoleCountDto>> countUsersByRole() {
        List<RoleCountDto> counts = userService.countUsersByRole();
        return ApiResponse.success(counts);
    }

    @Operation(summary = "统计用户数量超过指定值的角色", description = "统计用户数量超过 minCount 的角色及其数量。")

    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "统计成功"),

    })

    @GetMapping("/countByRole/min/{minCount}")

    public ApiResponse<List<RoleCountDto>> countUsersByRoleHavingMinCount(@PathVariable long minCount) {

        List<RoleCountDto> counts = userService.countUsersByRoleHavingMinCount(minCount);

        return ApiResponse.success(counts);

    }


    @Operation(summary = "根据姓名和角色查询用户", description = "根据用户的姓名和角色查询用户列表。")

    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),

    })

    @GetMapping("/search")

    public ApiResponse<List<UserResponse>> searchUsers(

            @Parameter(description = "用户姓名", required = true) @RequestParam String name,

            @Parameter(description = "用户角色", required = true) @RequestParam String role) {

        List<UserResponse> users = userService.findUsersByNameAndRole(name, role);

        return ApiResponse.success(users);

    }
}