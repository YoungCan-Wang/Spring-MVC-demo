# 2.4 API文档：Swagger 与 OpenAPI

## Q: 如何为我的 Spring Boot API 添加在线文档？

A: 在 Spring Boot 生态中，为 API 添加交互式在线文档的标准方案是 **OpenAPI 3** 规范和 **Swagger UI** 工具的结合。

- **OpenAPI 3**: 一个行业标准，用于以机器可读的格式描述 RESTful API 的结构（如接口路径、参数、返回值、错误码等）。
- **Swagger UI**: 一个前端应用，它可以读取 OpenAPI 规范文档，并生成一个美观、可交互的HTML页面，让开发者可以直接在浏览器中浏览和测试API。

### 实现步骤

在 Spring Boot 中，集成这一切非常简单，主要归功于 `springdoc-openapi` 这个优秀的社区库。

**1. 添加依赖**

在 `pom.xml` 中，只需添加一个依赖即可：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version> <!-- 版本号需与Spring Boot版本兼容 -->
</dependency>
```

**2. 运行与访问**

添加依赖后，无需任何额外配置。直接启动 Spring Boot 应用，然后通过浏览器访问以下两个路径：

- **API 文档页**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 规范 (JSON)**: `http://localhost:8080/v3/api-docs`

`springdoc` 库会自动扫描你所有的 `@RestController`，并根据代码生成完整的文档。

## 附录：一次棘手的 Swagger 500 错误排查过程

在集成过程中，我们遇到了一个典型的、由**依赖版本不兼容**导致的 `500 /v3/api-docs` 错误。这是一个非常有价值的实战案例。

**1. 症状**

- 访问 `/swagger-ui.html` 页面，UI框架能加载，但内容区报错 `Failed to load API definition`。
- 直接访问 `/v3/api-docs`，返回 500 错误。

**2. 排查过程**

我们遵循了一个标准的调试清单：

- **怀疑泛型**: 首先怀疑是 `ApiResponse<T>` 泛型导致解析失败，尝试让接口直接返回实体，但问题依旧。
- **建立最小基线**: 添加了一个极简的 `PingController`，但文档依然无法生成。这证明问题并非出在某个特定 Controller
  的复杂性上，而是更全局的问题。
- **怀疑全局异常处理器**: 接着怀疑 `GlobalExceptionHandler` 干扰了 `springdoc` 的内部流程。通过 `@Hidden`
  或限制扫描范围等“规避”手段可以绕过，但不是根本解决方案。
- **暴露根本原因**: 最终，我们在 `GlobalExceptionHandler` 中添加了日志，打印出了被它捕获的原始异常。日志显示，根本错误是
  `java.lang.NoSuchMethodError`。

**3. 根本原因**

`NoSuchMethodError` 是一个决定性的证据，它清晰地表明 `springdoc` 库的某个版本，与我们项目使用的 Spring Framework 版本之间存在
**二进制级别的不兼容**。`springdoc` 在分析 `@RestControllerAdvice` 时，试图调用一个在当前 Spring Framework
版本中不存在的构造方法，导致其内部崩溃。

**4. 最终解决方案**

解决方案是“修正版本，回归正道”。通过查阅资料或 gpt5 的建议，我们将 `springdoc` 的版本升级到了与项目使用的 Spring Boot /
Spring Framework 版本**精确兼容**的 `2.7.0`，问题从而得到根本解决。

这个案例教育我们，在遇到难以理解的“魔法”问题时，要善用“最小化复现”、“控制变量”和“查看原始日志”等经典调试手段来定位问题根源。
