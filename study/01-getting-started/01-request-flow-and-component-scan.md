# 1.1 请求流程与组件扫描

## Q: Spring Boot应用中，主程序（DemoApplication）是如何与控制器（UserController）关联起来的？

A: Spring Boot 与 Android 的机制不同，它不依赖于像 `AndroidManifest.xml` 这样的显式配置文件来注册组件，而是依赖于 **自动扫描（Component Scan）** 和 **依赖注入（Dependency Injection）**。

核心流程如下：

1.  **启动注解**: `DemoApplication` 上的 `@SpringBootApplication` 是启动一切的关键。
2.  **组合注解**: 这个注解实际上包含了 `@ComponentScan`。
3.  **自动扫描**: `@ComponentScan` 会指示 Spring 框架去自动扫描 `DemoApplication` 所在的包（即 `com.example.demo`）以及其所有子包。
4.  **发现组件**: 当扫描到 `com.example.demo.controller.UserController` 时，Spring 发现它被 `@RestController` 注解标记。
5.  **自动注册**: Spring 会自动将这个 `UserController` 类实例化，并作为一个 Bean（一个由Spring管理的对象）注册到内部的 IoC（Inversion of Control）容器中。
6.  **请求分发**: 当内置的 Tomcat 服务器启动后，Spring 的 `DispatcherServlet` 会知道所有已注册的 Controller 和它们能处理的URL路径（例如 `/user/**`）。当一个匹配的HTTP请求进来时，它就会被准确地分发到 `UserController` 中对应的方法。

**总结：** 关联是 **隐式** 的，由框架自动完成。只要 Controller 位于正确的包路径下并拥有正确的注解，Spring Boot 就能自动发现并管理它，无需任何手动注册。

### Spring Boot 请求处理流程图

```
DemoApplication.main()
      ↓
SpringApplication.run(...)
      ↓
创建 IoC 容器
      ↓
从包 com.example.demo.* 开始扫描所有类
      ↓
找到 UserController (标注了 @RestController)
      ↓
注册为 Bean（Spring管理对象）
      ↓
启动内置 Tomcat
      ↓
DispatcherServlet 拿到所有 Controller 映射关系
      ↓
访问 /user/** → 自动分发到 UserController 对应方法
```
