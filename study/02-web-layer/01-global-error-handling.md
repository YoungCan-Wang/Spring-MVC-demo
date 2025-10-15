# 2.1 全局异常处理

## Q: 为什么访问一个不存在的URL或用错误的方法请求接口时，会看到一个 "Whitelabel Error Page"？是否有必要自己创建一个/error接口来处理所有错误？

A: 这个问题的背后是 Spring Boot 的默认错误处理机制，并且引出了现代Web应用中一个非常重要的概念：**全局统一异常处理**。

### 1. "Whitelabel Error Page" 的由来

Spring Boot 内置了一个默认的错误处理器（`BasicErrorController`），它会接管所有未被处理的异常（例如 404 Not Found, 405 Method Not Allowed, 500 Internal Server Error 等）。

当这类错误发生时，Spring Boot 的处理流程是：
1.  发生错误（如访问了不存在的 `/foo` 路径）。
2.  Spring Boot 捕获这个错误，并将请求 **转发（forward）** 到一个特定的路径：`/error`。
3.  它尝试寻找一个由开发者定义的、能够处理 `/error` 路径的 Controller 方法或视图。
4.  如果 **没有找到** 任何自定义的 `/error` 处理器，Spring Boot 就会渲染一个默认的、非常简陋的HTML页面，也就是 "Whitelabel Error Page"。

页面上 "This application has no explicit mapping for /error" 这句话的意思就是：“我尝试去找 /error 了，但你没写，所以我只能给你看这个默认的白板页面了。”

### 2. 最佳实践：使用 `@RestControllerAdvice`

直接去实现一个 `/error` 接口来处理所有错误是一种比较原始的方式，难以覆盖所有异常情况。

现代 Spring Boot 应用的最佳实践是使用 **全局异常处理器**。这通过创建一个带有 `@RestControllerAdvice` 注解的类来实现。

-   `@ControllerAdvice` / `@RestControllerAdvice`：这是一个全局性的注解，允许你用一个集中的机制来处理多个 Controller 中抛出的异常。
-   `@ExceptionHandler(Exception.class)`：在这个类中，你可以创建方法，并用 `@ExceptionHandler` 注解来指定这个方法具体处理哪一种（或哪一类）异常。

### 3. 为什么 `@RestControllerAdvice` 更好？

-   **关注点分离**：将错误处理逻辑从业务逻辑（Controller）中完全分离出来。
-   **统一响应格式**：可以确保无论发生什么错误（参数错误、业务异常、服务器内部错误），返回给前端（如你的Android App）的都是一个格式统一的JSON对象（例如 `ApiResponse`），而不是一个HTML错误页面。这对于API开发至关重要。
-   **代码复用**：避免在每个Controller方法中都写 `try-catch`。

### 4. 与 Android 开发的类比

这种机制可以完美地与你熟悉的 Android 网络请求处理方式对应：

| Android 客户端 (Retrofit/OkHttp) | Spring Boot 服务端 |
| :--- | :--- |
| 全局 OkHttp Interceptor 统一处理错误 | `@RestControllerAdvice` 全局处理异常 |
| `onFailure` 回调处理网络或服务器异常 | `@ExceptionHandler(Exception.class)` 捕获并处理异常 |
| `Response<T>` 对象包含 code + body | `ApiResponse<T>` 对象包含 code + data |
| 根据 `response.code()` 判断业务失败 | 通过 `HttpStatus` 或自定义错误码返回状态 |

### 总结

| 行为 | 不推荐的做法 | **推荐/标准做法** |
| :--- | :--- | :--- |
| 处理应用中发生的各种错误 | 手动在每个Controller中`try-catch` | 使用 `@RestControllerAdvice` 做全局异常处理 |
| 错误发生时返回给客户端 | 返回HTML错误页 (Whitelabel Page) | 返回统一格式的JSON错误信息 (e.g., `ApiResponse`) |
| 在浏览器中测试POST接口 | 在地址栏输入URL（这只会发GET请求） | 使用 Postman, cURL, 或 IDE 自带的 HTTP Client 工具 |

因此，你完全不需要自己实现一个 `/error` 接口。引入一个 `GlobalExceptionHandler` 类才是更专业、更彻底的解决方案。
