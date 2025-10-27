# 第 7 课：HikariCP 连接池配置与优化

## 1. 什么是数据库连接池？

数据库连接池是一种资源管理技术，它维护一定数量的数据库连接，供应用程序重复使用。

### 为什么需要连接池？
- **性能提升**：避免频繁创建/销毁连接的开销
- **资源控制**：限制并发连接数，防止数据库过载
- **连接复用**：多个请求共享连接，提高资源利用率

### 没有连接池的问题：
```java
// 每次都创建新连接，性能很差
Connection conn = DriverManager.getConnection(url, user, password);
// 使用连接
conn.close(); // 真正关闭连接
```

### 使用连接池：
```java
// 从池中获取连接，速度很快
Connection conn = dataSource.getConnection();
// 使用连接
conn.close(); // 归还给连接池，不是真正关闭
```

## 2. HikariCP 简介

HikariCP 是目前最快的 Java 数据库连接池，Spring Boot 2.0+ 默认使用它。

### 优势：
- **性能最佳**：比其他连接池快 2-3 倍
- **轻量级**：代码简洁，依赖少
- **稳定可靠**：经过大量生产环境验证
- **零开销**：几乎没有性能损耗

## 3. 核心配置参数

### 连接池大小配置
```yaml
spring:
  datasource:
    hikari:
      # 连接池最大连接数（默认10）
      maximum-pool-size: 20
      
      # 连接池最小空闲连接数（默认与maximum-pool-size相同）
      minimum-idle: 5
      
      # 连接池名称
      pool-name: "MyHikariPool"
```

### 连接超时配置
```yaml
spring:
  datasource:
    hikari:
      # 获取连接的最大等待时间（毫秒，默认30秒）
      connection-timeout: 30000
      
      # 连接空闲超时时间（毫秒，默认10分钟）
      idle-timeout: 600000
      
      # 连接最大生命周期（毫秒，默认30分钟）
      max-lifetime: 1800000
```

### 连接验证配置
```yaml
spring:
  datasource:
    hikari:
      # 连接测试查询（MySQL）
      connection-test-query: SELECT 1
      
      # 验证连接有效性的超时时间（毫秒）
      validation-timeout: 5000
```

## 4. 参数详解与调优

### maximum-pool-size（最大连接数）
- **作用**：连接池能创建的最大连接数
- **调优原则**：
  ```
  最佳连接数 = CPU核心数 × 2 + 磁盘数
  ```
- **示例**：8核CPU + 1个磁盘 = 8 × 2 + 1 = 17个连接

### minimum-idle（最小空闲连接）
- **作用**：连接池维持的最小空闲连接数
- **建议**：设置为 maximum-pool-size 的 50%-80%
- **注意**：设置过高浪费资源，过低影响响应速度

### connection-timeout（连接超时）
- **作用**：从连接池获取连接的最大等待时间
- **建议**：30秒（30000ms）通常足够
- **注意**：设置过短可能导致高并发时获取连接失败

### idle-timeout（空闲超时）
- **作用**：连接空闲多久后被回收
- **建议**：10分钟（600000ms）
- **注意**：设置过短频繁创建连接，过长占用资源

### max-lifetime（最大生命周期）
- **作用**：连接的最大存活时间
- **建议**：30分钟（1800000ms）
- **注意**：应该比数据库的连接超时时间短

## 5. 实际配置示例

### 开发环境配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo
    username: root
    password: password
    hikari:
      maximum-pool-size: 5      # 开发环境连接数少
      minimum-idle: 2
      connection-timeout: 20000
      idle-timeout: 300000      # 5分钟
      max-lifetime: 900000      # 15分钟
      pool-name: "DevHikariPool"
```

### 生产环境配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/demo
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20     # 生产环境需要更多连接
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000      # 10分钟
      max-lifetime: 1800000     # 30分钟
      pool-name: "ProdHikariPool"
      
      # 生产环境额外配置
      leak-detection-threshold: 60000  # 连接泄漏检测（1分钟）
      connection-test-query: SELECT 1
```

## 6. 监控和诊断

### 启用连接池监控
```yaml
spring:
  datasource:
    hikari:
      # 启用JMX监控
      register-mbeans: true
      
      # 连接泄漏检测阈值（毫秒）
      leak-detection-threshold: 60000
```

### 查看连接池状态
```java
@RestController
public class PoolMonitorController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/pool-status")
    public Map<String, Object> getPoolStatus() {
        HikariDataSource hikariDS = (HikariDataSource) dataSource;
        HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();
        
        Map<String, Object> status = new HashMap<>();
        status.put("activeConnections", poolBean.getActiveConnections());
        status.put("idleConnections", poolBean.getIdleConnections());
        status.put("totalConnections", poolBean.getTotalConnections());
        status.put("threadsAwaitingConnection", poolBean.getThreadsAwaitingConnection());
        
        return status;
    }
}
```

## 7. 常见问题和解决方案

### 问题1：连接池耗尽
```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```
**解决方案**：
- 增加 `maximum-pool-size`
- 检查是否有连接泄漏
- 优化慢查询

### 问题2：连接泄漏
```
HikariPool-1 - Connection leak detection triggered
```
**解决方案**：
- 确保所有连接都正确关闭
- 使用 try-with-resources 语句
- 启用 `leak-detection-threshold`

### 问题3：连接频繁创建销毁
**解决方案**：
- 调整 `minimum-idle` 参数
- 增加 `idle-timeout` 时间
- 检查应用的连接使用模式

## 8. 性能调优建议

### 1. 连接数计算公式
```
connections = ((core_count * 2) + effective_spindle_count)
```

### 2. 监控关键指标
- 活跃连接数
- 等待连接的线程数
- 连接获取时间
- 连接使用率

### 3. 压力测试验证
```bash
# 使用 JMeter 或 ab 进行压力测试
ab -n 1000 -c 50 http://localhost:8080/api/users/1
```

## 9. 下一步学习

完成连接池配置后，下一步将学习：
- MyBatis 集成和配置
- 动态 SQL 编写
- MyBatis 与 JPA 的对比
