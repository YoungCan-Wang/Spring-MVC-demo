package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository  // 标记为数据层 Bean
public class UserRepository {

    private final Map<Integer, User> storage = new HashMap<>();

    public User findById(int id) {
        return storage.getOrDefault(id, new User(id, "User_" + id, "Guest"));
    }

    public void save(User user) {
        storage.put(user.getId(), user);
    }
}
