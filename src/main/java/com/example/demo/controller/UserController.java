package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    // 接收 JSON 请求体，并返回 JSON 响应
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // 这里简单返回对象本身
        return user;
    }

    // 简单的 GET 接口
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return new User(id, "User_" + id, "Android Developer");
    }
}

// 内部类：数据模型
class User {
    private int id;
    private String name;
    private String role;

    // 必须有无参构造 & getter/setter 才能被 Spring 解析 JSON
    public User() {}

    public User(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
