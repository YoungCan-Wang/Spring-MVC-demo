# 2.3 声明式数据校验

## Q: 服务端如何进行参数校验？和Android中的手动校验有何不同？

A: 在Spring Boot等现代后端框架中，数据校验是**声明式**的，并且与框架高度集成，这与Android中常见的手动、UI驱动的校验有很大不同。

### 核心理念：AOP 与“约定优于配置”

我们不需要手动编写 `if (name == null)` 这样的代码，而是通过注解来“声明”我们的校验规则。框架通过 **AOP (面向切面编程)**
技术，在调用我们的 Controller 方法**之前**，自动拦截请求，并根据注解执行校验逻辑。

- **开发者**: 只负责声明规则 (e.g., `@NotBlank`)。
- **框架**: 负责在合适的时机执行校验，并在失败时抛出标准异常。

这种模式将校验的“业务规则”与“执行逻辑”解耦，是框架“约定优于配置”思想的完美体现。

### 实现步骤

实现一个完整的、优雅的数据校验流程分为三步：

**1. 添加依赖**

在 `pom.xml` 中加入 `spring-boot-starter-validation`，它包含了所有必需的库（如 `hibernate-validator`）。

**2. 在 DTO 中声明规则**

在作为API输入的 DTO 类（或 `record`）的字段上，添加来自 `jakarta.validation.constraints` 包的注解。

```java
public record UserCreateRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 2, max = 32, message = "Username must be between 2 and 32 characters")
        String name,

        @NotBlank(message = "Role cannot be blank")
        String role
) {}
```

**3. 在 Controller 中触发校验**

在 Controller 方法中，给需要被校验的 `@RequestBody` 参数加上 `@Valid` 注解。

```java
public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreateRequest createRequest) {
    // ...
}
```

当请求到达时，Spring看到 `@Valid`，就会自动对 `createRequest` 对象执行校验。

**4. 全局处理校验异常**

如果校验失败，Spring会抛出 `MethodArgumentNotValidException`。我们需要在 `@RestControllerAdvice`
全局异常处理器中专门捕获这个异常，并返回一个结构化的、对客户端友好的 `400 Bad Request` 响应。

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
    });
    return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
}
```

通过这四步，我们就构建了一套完整、健壮、可复用且与业务逻辑解耦的自动化数据校验体系。
