# 事务管理最佳实践

## 1. 事务注解放置位置

### ✅ 推荐：Service 层
```java
@Service
@Transactional(readOnly = true) // 默认只读
public class UserService {
    
    @Transactional // 写操作覆盖为读写事务
    public void createUser(UserCreateRequest request) {
        // 业务逻辑
    }
}
```

### ❌ 不推荐：Controller 层
```java
@RestController
public class UserController {
    
    @Transactional // 不要在Controller层使用
    public ResponseEntity<?> createUser() {
        // 事务边界太宽，包含了HTTP处理
    }
}
```

## 2. 事务粒度控制

### ✅ 合适的事务粒度
```java
@Transactional
public void transferMoney(int fromUserId, int toUserId, BigDecimal amount) {
    // 一个完整的业务操作在一个事务中
    accountService.debit(fromUserId, amount);
    accountService.credit(toUserId, amount);
    logService.recordTransfer(fromUserId, toUserId, amount);
}
```

### ❌ 事务粒度过大
```java
@Transactional
public void processAllUsers() {
    List<User> users = userRepository.findAll(); // 可能很多数据
    for (User user : users) {
        // 长时间运行的事务，占用数据库连接
        processUser(user);
    }
}
```

## 3. 异常处理

### ✅ 让异常传播，触发回滚
```java
@Transactional
public void createUser(UserCreateRequest request) {
    User user = new User();
    user.setName(request.name());
    userRepository.save(user);
    
    // 如果这里抛出异常，整个事务回滚
    sendWelcomeEmail(user.getEmail());
}
```

### ❌ 捕获异常阻止回滚
```java
@Transactional
public void createUser(UserCreateRequest request) {
    try {
        User user = new User();
        userRepository.save(user);
        sendWelcomeEmail(user.getEmail()); // 可能失败
    } catch (Exception e) {
        // 捕获异常阻止了事务回滚！
        log.error("Error creating user", e);
    }
}
```

## 4. 只读事务优化

### ✅ 查询方法使用只读事务
```java
@Service
@Transactional(readOnly = true) // 类级别默认只读
public class UserService {
    
    // 继承只读事务，性能更好
    public List<User> findActiveUsers() {
        return userRepository.findByActiveTrue();
    }
    
    @Transactional // 写操作需要读写事务
    public User createUser(UserCreateRequest request) {
        // 写操作
    }
}
```

## 5. 事务传播的实际应用

### 场景：审计日志独立事务
```java
@Service
public class UserService {
    
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
        
        // 审计日志使用独立事务，即使主业务失败也要记录
        auditService.logUserUpdate(user.getId());
    }
}

@Service
public class AuditService {
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUserUpdate(int userId) {
        // 独立事务，不受主事务影响
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction("UPDATE");
        auditRepository.save(log);
    }
}
```

## 6. 常见错误和解决方案

### 错误1：同类方法调用
```java
@Service
public class UserService {
    
    public void publicMethod() {
        // 这个调用不会触发事务！
        this.transactionalMethod();
    }
    
    @Transactional
    private void transactionalMethod() {
        // 事务不会生效
    }
}
```

**解决方案**：提取到不同的Service类

### 错误2：检查异常不回滚
```java
@Transactional
public void createUser() throws IOException {
    userRepository.save(new User());
    
    // IOException 不会触发回滚！
    throw new IOException("File not found");
}
```

**解决方案**：指定回滚异常
```java
@Transactional(rollbackFor = Exception.class)
public void createUser() throws IOException {
    // 现在所有异常都会回滚
}
```

## 7. 性能考虑

### 事务超时设置
```java
@Transactional(timeout = 30) // 30秒超时
public void longRunningOperation() {
    // 防止长时间占用数据库连接
}
```

### 批量操作优化
```java
@Transactional
public void batchCreateUsers(List<UserCreateRequest> requests) {
    // 批量操作在一个事务中，提高性能
    List<User> users = requests.stream()
        .map(this::convertToUser)
        .collect(Collectors.toList());
    
    userRepository.saveAll(users); // 批量保存
}
```

## 8. 测试事务代码

```java
@SpringBootTest
@Transactional // 测试事务会自动回滚
class UserServiceTest {
    
    @Test
    void testCreateUser() {
        // 测试代码
        userService.createUser(request);
        
        // 测试结束后自动回滚，不影响其他测试
    }
    
    @Test
    @Rollback(false) // 如果需要提交测试数据
    void testWithCommit() {
        // 这个测试的数据会真正提交
    }
}
```
