package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Data JPA 会自动实现所有基础的 CRUD 方法
    // 我们甚至不需要在这里定义 findById, save 等方法，它们是继承自 JpaRepository 的

    // 如果需要特殊的查询，比如通过名字查找用户，可以像下面这样定义一个方法：
    // User findByName(String name);
    // Spring Data JPA 会根据方法名自动生成查询语句，非常强大
}