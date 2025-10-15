# 1.3 依赖注入与控制反转

## Q: 我没有手动创建`UserService`的实例，它是如何与`UserRepository`关联起来的？

A: 这是Spring框架最核心的功能：**依赖注入 (Dependency Injection, DI)** 和 **控制反转 (Inversion of Control, IoC)**。

你不需要手动创建和组装这些对象，Spring会像一个智能管家一样在后台为你完成。

### Spring的自动化装配流程

1.  **扫描 (Component Scan)**
    *   应用启动时，Spring扫描所有被`@Component`, `@Service`, `@Repository`, `@Controller`等注解标记的类。

2.  **注册`UserRepository`**
    *   Spring发现`UserRepository`类及其`@Repository`注解。
    *   它会自动创建一个`UserRepository`的实例（一个Bean），并放入一个叫做“IoC容器”的中央仓库中进行管理。

3.  **分析并注册`UserService`**
    *   接着，Spring发现`UserService`类及其`@Service`注解。
    *   在创建实例前，Spring会检查它的构造函数 `public UserService(UserRepository userRepository)`。
    *   Spring发现：“要创建`UserService`，必须先提供一个`UserRepository`的实例。”

4.  **注入依赖 (DI)**
    *   Spring自动在它的“IoC容器”中寻找一个`UserRepository`类型的Bean。
    *   它找到了第2步中创建的那个实例，并自动将它作为参数传递给`UserService`的构造函数来创建`UserService`实例。
    *   这个“由容器提供依赖，而不是自己创建依赖”的过程，就是**依赖注入**。

5.  **完成**
    *   `UserService`实例被成功创建并放入IoC容器，随时可以被其他组件（如`UserController`）注入使用。

### 总结

你所做的只是：
1.  用**注解** (`@Service`, `@Repository`) 为组件**“贴标签”**。
2.  用**构造函数**声明组件的**“依赖项”**。

剩下的创建、组装工作都由Spring框架自动完成。开发者不再控制对象的创建流程，这个“控制权”被“反转”给了框架，这就是**控制反转 (IoC)**。

---

## Q: Controller, Service, Repository的实例创建有先后顺序么？

A: 对开发者来说没有顺序要求，但Spring框架内部会自动计算出正确的创建顺序。它不是按文件或名称排序，而是通过分析依赖关系来决定。

### Spring的Bean创建逻辑

1.  **第一阶段：扫描与定义**
    *   应用启动时，Spring先扫描所有组件类（如`@Controller`, `@Service`等），但**不立即创建实例**。它首先在内部将这些类注册为“Bean定义”。

2.  **第二阶段：构建依赖图**
    *   通过分析所有“Bean定义”的构造函数和依赖关系，Spring在内存中构建一个依赖图。在我们的项目中，这个图是： `UserController` -> `UserService` -> `UserRepository`。

3.  **第三阶段：按图实例化**
    *   Spring从**没有依赖的Bean**开始创建实例。
    *   **Step 1**: `UserRepository`不依赖任何东西，最先被创建。
    *   **Step 2**: `UserService`依赖的`UserRepository`已就绪，所以`UserService`被创建。
    *   **Step 3**: `UserController`依赖的`UserService`已就绪，所以`UserController`最后被创建。

这个机制保证了在创建任何一个组件时，它所需要的所有依赖都已经被提前创建好了。

此外，这个机制还能在启动时就检测出“循环依赖”（如A依赖B，B又依赖A）这种不合理的设计，并抛出异常，从而保证了代码的健壮性。