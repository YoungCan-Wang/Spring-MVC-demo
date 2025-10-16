# Spring-MVC-demo
一个android原生开发对Spring MVC的学习

---

# Spring Boot 学习项目 (Spring Boot Learning Project)

本项目是一个现代化的、准生产级的 Spring Boot 应用，通过渐进式开发，用于学习和展示后端开发的核心概念。项目本身是一个用于管理用户的
RESTful API。

## 核心概念实现 (Core Concepts Implemented)

本项目完整地展示了一套功能齐全且健壮的API实现，包含了以下核心技术与思想：

- **分层架构 (Layered Architecture)**: 清晰的三层架构，实现了职责分离。
    - `Controller` (控制层): 处理 HTTP 请求、校验及 DTO 转换。
    - `Service` (服务层): 包含核心的、带事务的业务逻辑。
    - `Repository` (仓储层): 使用 Spring Data JPA 管理数据持久化。

- **数据库持久化 (Database Persistence)**:
    - **Spring Data JPA**: 将接口转化为功能完备的 Repository Bean，极大简化了数据库交互。
    - **H2 内存数据库 (In-Memory Database)**: 用于快速开发和测试，无需安装外部数据库。
    - **JPA 实体 (JPA Entities)**: 使用 `@Entity` 注解将 `User` 类映射为数据库表。

- **现代化DTO设计 (Modern DTO Design)**:
    - **Java 17 `record`**: 用于创建简洁、不可变的数据传输对象 (DTO)，如 `UserResponse`。
    - **MapStruct**: 用于自动化、类型安全且高性能地完成 DTO 与实体类之间的映射，消除了大量样板代码。

- **API 与错误处理 (API & Error Handling)**:
    - **请求校验 (Request Validation)**: 使用 `spring-boot-starter-validation` 和 `@Valid` 注解自动校验输入数据。
    - **全局异常处理 (Global Exception Handling)**: 通过 `@RestControllerAdvice` 为不同类型的异常提供了一致的、结构化的JSON错误响应。
    - **自定义错误码 (Custom Error Codes)**: 建立了一套基于 `enum` 的错误码体系，实现了与HTTP状态码解耦的、机器可读的业务错误识别。

- **API文档 (API Documentation)**:
    - **Swagger UI (OpenAPI 3)**: 集成 `springdoc-openapi`，根据代码自动生成可交互的在线 API 文档。

- **Spring 核心概念 (Core Spring Concepts)**:
    - **依赖注入/控制反转 (DI/IoC)**: 全面使用构造函数注入，实现健壮、可测试的组件装配。
    - **分层配置 (Configuration)**: 使用 `application.yml` 进行清晰的、层级化的应用配置。

## API 端点 (API Endpoints)

- `GET /user/{id}`: 根据ID查询用户。
- `POST /user/register`: 注册一个新用户。

> 完整的、可交互的API文档地址位于:
> **http://localhost:8080/swagger-ui.html**

## 如何运行 (How to Run)

1. **环境要求 (Prerequisites)**:
    - Java 17 或更高版本
    - Maven

2. **执行 (Execution)**:
   使用项目自带的 Maven Wrapper 即可运行本项目：

    ```bash
    ./mvnw spring-boot:run
    ```

   应用将在 `http://localhost:8080` 端口启动。
