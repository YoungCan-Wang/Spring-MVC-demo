# 1.4 配置文件：properties 与 yml

## Q: `application.properties` 和 `application.yml` 有什么区别和联系？

A: 它们是Spring Boot中两种不同格式但作用完全相同的配置文件，开发者可以任选其一。

### 核心关系

- **目标一致**: 都是用来为Spring Boot应用提供外部化配置（如服务器端口、数据库连接信息等）。
- **二选一**: 通常在一个项目中只使用一种。如果两种文件同时存在于`src/main/resources`目录下，`.properties`文件的优先级 **高于
  ** `.yml`文件。

### 格式区别

这是两者最直观的不同：

**1. `application.properties`**

- **语法**: `key=value`
- **特点**: 传统的键值对格式，所有配置都是扁平的，通过完整的路径名来区分层级。

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
```

**2. `application.yml` (或 `.yaml`)**

- **语法**: `key: value`，使用**空格缩进**来表示层级。
- **特点**: YAML 格式，具有天然的树状结构，当配置项复杂且数量多时，可读性远超 `.properties`。

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
```

### 总结与最佳实践

| 对比项     | `.properties` | `.yml`            | 结论              |
|:--------|:--------------|:------------------|:----------------|
| **作用**  | 应用配置          | 应用配置              | **完全相同**        |
| **语法**  | `key=value`   | `key: value` + 缩进 | `.yml`可读性更好，更流行 |
| **优先级** | **高**         | 低                 | 避免混用，只用一种       |

**实践建议**: 对于新项目，推荐优先使用 `.yml` 格式。它的结构化特性使得配置的组织和阅读都更加轻松，尤其是在微服务、多环境配置等复杂场景下，优势更为明显。
