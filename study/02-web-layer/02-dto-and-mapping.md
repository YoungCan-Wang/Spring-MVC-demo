# 2.2 DTO 设计与对象映射

## Q: 为什么要用 DTO？我们如何优化它？

A: 在 Controller 层和外部客户端之间，使用 DTO (Data Transfer Object) 而不是直接暴露数据库实体 (`@Entity`)，是后端开发的核心最佳实践之一。

### 1. 为什么要用 DTO？

- **安全**: 避免意外泄露数据库层面的敏感字段（如密码哈希、内部状态等）。
- **解耦**: 将内部数据模型 (`Entity`) 和外部API模型 (`DTO`) 分离开。这样，即使未来数据库结构发生变化，只要 DTO
  保持不变，API的调用方就不会受到影响。
- **灵活性**: 可以根据不同API的需求，定制不同的数据视图。例如，列表页可能只需要少量字段，而详情页需要更多字段。

### 2. 用 `record` 优化 DTO

对于 Java 14+ 的项目，使用 `record` 类型来定义 DTO 是一个巨大的进步。

**传统 Class DTO:**

```java
public class UserDTO {
    private Integer id;
    // ... getters, setters, constructor, equals, hashCode, toString
}
```

**现代 `record` DTO:**

```java
public record UserResponse(Integer id, String name, String role) {}
```

`record` 的优势：

- **简洁**: 编译器会自动生成构造函数、所有字段的 `getter`、`equals()`、`hashCode()` 和 `toString()` 方法，代码量极大减少。
- **不可变 (Immutable)**: `record` 的字段默认是 `final` 的，其实例一旦创建就无法修改。这使得程序行为更可预测，且天然线程安全。

### 3. 用 MapStruct 优化对象映射

在 Service 层，我们需要在 `Entity` 和 `DTO` 之间进行转换。手写 `get/set` 代码非常繁琐且容易出错。

**MapStruct** 是一个代码生成器，可以完美解决这个问题。

**工作流程**:

1. **定义接口**: 你只需要创建一个 `Mapper` 接口，并声明转换方法。
   ```java
   @Mapper(componentModel = "spring")
   public interface UserMapper {
       User toEntity(UserCreateRequest request);
       UserResponse toResponse(User user);
   }
   ```
2. **编译时生成代码**: 在编译项目时，MapStruct 会自动为你生成这个接口的实现类，其中包含了所有 `get/set` 的逻辑。
3. **注入并使用**: 由于我们使用了 `componentModel = "spring"`，生成的实现类会自动成为一个 Spring Bean，我们可以直接在
   `Service` 中注入并使用它。

**优势**:

- **消除样板代码**: 不再需要手写任何转换逻辑。
- **类型安全**: 如果源对象和目标对象的字段不匹配，编译时就会报错。
- **高性能**: 因为是编译期生成代码，其运行性能与手写代码完全相同。
