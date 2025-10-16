# 4.1 自定义错误码体系

## Q: 为什么要用自定义错误码？

A: HTTP 状态码（如 400, 404）对于网络层和浏览器是友好的，但对于需要根据不同业务错误做出不同反应的**客户端应用**来说，信息粒度太粗。

例如，一个 `404 Not Found` 无法区分是“用户不存在”还是“商品不存在”。

**自定义错误码**为每一种具体的业务异常分配一个应用内唯一的、稳定的数字码。这使得客户端可以通过判断这个机器可读的 `code`
，来精确地执行不同的业务逻辑（如页面跳转、特定UI提示等），而无需解析易变的 `message` 字符串。

## 实现步骤

构建一个优雅、健壮的自定义错误码体系，我们采用了四步走的策略：

**1. 创建 `ErrorCode` 枚举**

这是我们错误体系的“真理之源”。我们创建一个枚举类，其中每个成员都代表一个具体的错误，并同时封装了**自定义业务码**、**默认错误信息
**和对应的 **HTTP状态码**。

```java
public enum ErrorCode {
    USER_NOT_FOUND(1001, "指定用户不存在", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(2001, "参数校验失败", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(5000, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);

    // ... 构造函数和 getters
}
```

**2. 创建 `BusinessException`**

我们创建一个继承自 `RuntimeException` 的自定义异常类，它在构造时必须持有一个 `ErrorCode` 实例。

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    // ... 构造函数和 getter
}
```

**3. 在 Service 中使用**

在业务逻辑层（`UserService`），当遇到可预见的业务失败（如找不到用户）时，我们抛出这个携带了明确错误码的自定义异常。

```java
// in UserService.java
public UserResponse getUserById(int id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toResponse(user);
}
```

**4. 在 `GlobalExceptionHandler` 中终极处理**

这是将一切串联起来的关键。我们在全局异常处理器中：

- 创建一个专门处理 `BusinessException` 的方法。
- 让所有处理方法都返回 `ResponseEntity<ApiResponse<...>>`，这是一个 Spring 的对象，可以让我们完全控制整个HTTP响应（包括状态码、头和响应体）。
- 在 `BusinessException` 处理器中，从异常里取出 `ErrorCode`，然后用它的各个属性来构建 `ResponseEntity`。

```java
// in GlobalExceptionHandler.java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
    ErrorCode errorCode = ex.getErrorCode();
    logger.warn("业务异常: {}", errorCode.getMessage());
    ApiResponse<Object> body = ApiResponse.fail(errorCode.getCode(), errorCode.getMessage(), null);
    // 使用 errorCode 中的 httpStatus 来设置响应的 HTTP 状态码
    return new ResponseEntity<>(body, errorCode.getHttpStatus());
}
```

通过这个四步流程，我们就构建了一套既符合 RESTful 规范（通过HTTP状态码），又对客户端应用极其友好（通过自定义业务码）的专业级错误处理体系。
