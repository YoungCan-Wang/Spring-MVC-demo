# 第 5 课：@Transactional 原理与事务实验

## 1. 什么是数据库事务？

事务是数据库操作的基本单位，它具有 **ACID** 特性：

- **原子性 (Atomicity)**: 事务中的所有操作要么全部成功，要么全部失败回滚
- **一致性 (Consistency)**: 事务执行前后，数据库状态保持一致
- **隔离性 (Isolation)**: 并发事务之间相互隔离，不会互相干扰
- **持久性 (Durability)**: 事务提交后，数据永久保存到数据库

## 2. Spring 的 @Transactional 注解

Spring 通过 `@Transactional` 注解提供声明式事务管理，它基于 **AOP (面向切面编程)** 实现。

### 工作原理：
1. Spring 为带有 `@Transactional` 的类创建代理对象
2. 当调用事务方法时，代理会：
   - 开启事务
   - 执行业务方法
   - 如果成功则提交事务，如果异常则回滚事务

### 常用属性：
```java
@Transactional(
    propagation = Propagation.REQUIRED,  // 事务传播行为
    isolation = Isolation.DEFAULT,       // 隔离级别
    rollbackFor = Exception.class,       // 回滚条件
    timeout = 30                         // 超时时间(秒)
)
```

## 3. 事务传播行为

- **REQUIRED** (默认): 如果当前有事务，加入该事务；如果没有，创建新事务
- **REQUIRES_NEW**: 总是创建新事务，如果当前有事务则挂起
- **SUPPORTS**: 如果当前有事务，加入该事务；如果没有，以非事务方式执行

## 4. 实验场景

我们将通过以下实验来理解事务：

1. **正常事务提交**：多个数据库操作全部成功
2. **事务回滚**：操作过程中抛出异常，所有操作回滚
3. **事务传播**：方法间的事务传播行为
4. **只读事务**：优化查询性能

## 5. 实验结果总结

### ✅ 成功验证的概念：

#### 实验1：正常事务提交
```bash
curl -X POST http://localhost:8080/api/transaction/test-success
# 结果：两个用户在同一事务中成功创建
```

#### 实验2：事务回滚（核心实验）
```bash
curl -X POST http://localhost:8080/api/transaction/test-rollback
# 结果：虽然执行了 save() 操作，但异常触发回滚，数据未保存
# 证明：事务的原子性 - 要么全部成功，要么全部失败
```

#### 实验3：事务传播
```bash
curl -X POST http://localhost:8080/api/transaction/test-propagation
# 结果：两个方法在同一事务中执行，用户数量增加2个
```

### 🔍 重要发现：

#### Spring AOP 限制
- **同一个类内部方法调用不会触发事务代理**
- 这是因为 Spring AOP 基于代理模式，内部调用绕过了代理
- 解决方案：将事务方法提取到不同的 Service 类中

#### 事务注解最佳实践
```java
@Service
@Transactional(readOnly = true) // 类级别：默认只读事务
public class UserService {
    
    @Transactional // 方法级别：覆盖类级别，使用读写事务
    public UserResponse createUser(UserCreateRequest request) {
        // 写操作
    }
    
    // 查询方法自动使用只读事务（继承类级别）
    public UserResponse getUserById(int id) {
        // 只读操作
    }
}
```

## 6. 关键学习点

### 事务回滚机制
- 默认只对 `RuntimeException` 和 `Error` 回滚
- 检查异常（Checked Exception）不会触发回滚
- 可通过 `rollbackFor` 属性自定义回滚条件

### 性能优化
- 只读事务 `@Transactional(readOnly = true)` 可以优化查询性能
- 数据库可以针对只读事务进行优化（如不加锁）

### 事务边界
- 事务边界应该在 Service 层，不是 Controller 或 Repository
- 一个业务操作对应一个事务

## 7. 注意事项

- `@Transactional` 只对 `public` 方法有效
- 同一个类内部方法调用不会触发事务代理（Spring AOP 限制）
- 默认只对 `RuntimeException` 和 `Error` 回滚，不对检查异常回滚
- 事务方法不应该捕获异常，否则会阻止回滚

## 8. 下一步学习

完成事务管理后，下一步将学习：
- HikariCP 连接池配置
- 数据库连接池参数调优
- 监控数据库连接状态
