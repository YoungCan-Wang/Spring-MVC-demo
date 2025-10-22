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

## 5. 注意事项

- `@Transactional` 只对 `public` 方法有效
- 同一个类内部方法调用不会触发事务代理（Spring AOP 限制）
- 默认只对 `RuntimeException` 和 `Error` 回滚，不对检查异常回滚
