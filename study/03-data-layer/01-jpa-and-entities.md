# 3.1 JPA 核心概念与实体设计

## Q: 什么是 JPA？

A: JPA (Jakarta Persistence API) 是一套官方的 **Java 规范**，它不是一个具体的框架，而是一套指导如何进行对象关系映射（ORM）的
**“标准和规则”**。

- **ORM (Object-Relational Mapping)**: 一种编程技术，用于在“Java 的对象模型”和“关系型数据库的表模型”之间进行自动化的数据转换。

- **核心思想**: 让你用操作 Java 对象的方式（`new User()`, `user.setName(...)`）来间接操作数据库表，而无需手写繁琐的 SQL。

- **规范 vs 实现**: JPA 本身只是一套接口（如 `EntityManager`）和注解（如 `@Entity`）。我们需要一个实现了这些接口和注解功能的具体框架来完成实际工作。
  **Hibernate** 就是目前最流行的 JPA 实现，也是 Spring Boot 的默认选择。

    - **比喻**: JPA 是 **USB-C 接口标准**，Hibernate 则是具体某品牌的 **USB-C 充电器**。

## Q: 不同写法的 `User` 实体类有什么区别？

A: 对比两个版本的 `User` 实体，可以看出 JPA 实体设计的几个核心要点。

| 对比项           | 版本 A (`@GeneratedValue`) | 版本 B (手动设置ID)            | 分析与结论                                                                                     |
|:--------------|:-------------------------|:-------------------------|:------------------------------------------------------------------------------------------|
| **1. 表名映射**   | 默认 (`user`)              | `@Table(name = "users")` | **版本 B 更优**。明确指定表名是生产好习惯，可以避免命名冲突和不规范。                                                    |
| **2. 主键生成**   | `@GeneratedValue`        | 无此注解                     | **代表不同策略**。版本 A 让数据库**自动生成ID**，适合创建无业务主键的新数据。版本 B 需要**程序手动设置ID**，适合ID本身就是业务一部分的场景（如身份证号）。 |
| **3. ID数据类型** | `int`                    | `Integer`                | **版本 B 更优**。包装类 `Integer` 可以为 `null`，能清晰表示一个“尚未持久化”的新对象状态，而基本类型 `int` 默认为 `0`，可能引起混淆。     |

### 最终方案：融合两者优点

在我们的项目中，最终采用了融合方案，集两者之长：

```java

@Entity
@Table(name = "users") // 采纳 B：明确表名
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 采纳 A：数据库自动生成ID
    private Integer id; // 采纳 B：使用包装类
    // ...
}
```

这个最终版本拥有了明确的表名映射、健壮的ID类型和便捷的ID自动生成策略，是许多实际项目中的标准实践。

---

## Q: 为什么在POST请求中提供ID会导致 `unsaved-value mapping was incorrect` 错误？

A: 这是一个经典的JPA/Hibernate问题，根本原因在于 **ID生成策略** 与 **对象状态** 之间的冲突。

### JPA/Hibernate 的工作逻辑

当你调用 `repository.save(entity)` 方法时，JPA的实现（如Hibernate）需要判断这个 `entity` 究竟是一个需要 `INSERT`
的新对象，还是一个需要 `UPDATE` 的已存在对象。它的判断依据就是 **ID 字段的值**。

1. **ID 为 `null` 或 `0`**: 如果实体的ID字段是 `null`（对于包装类型如 `Integer`）或 `0`（对于原始类型如 `int`
   ），Hibernate会认为这是一个**新实体**，并准备执行 `INSERT` 操作。
2. **ID 有值 (非 `null` 且非 `0`)**: 如果ID字段有一个具体的值（比如 `1`），Hibernate会认为这是一个**已存在的实体**
   （处于游离态），并准备执行 `UPDATE` 操作。

### 错误发生过程

在我们的项目中：

1. `User` 实体配置了 `@GeneratedValue`，这告诉JPA：“ID由数据库自动生成，程序不应干预”。
2. 你用 `curl` 发送了一个包含 `"id":1` 的JSON。
3. 后端收到的 `User` 对象，其 `id` 字段的值就是 `1`。
4. 当 `save(user)` 方法被调用时，Hibernate看到 `id` 为 `1`，于是它认为这是一个已存在的用户，并尝试去数据库中 `UPDATE` 这条记录。
5. 然而，数据库是空的，根本不存在 `id=1` 的用户。这种“你让我更新一个我找不到的东西”的矛盾状态，最终导致了
   `unsaved-value mapping was incorrect` 异常。

### 正确的做法

当ID被配置为由数据库自动生成时，**客户端在创建新资源时，绝对不能包含 `id` 字段**。`id` 是由服务端（数据库）在持久化成功后生成并返回给客户端的。

**正确请求体**:

```json
{
    "name": "Hanzhi",
    "role": "Android Dev"
}
```

这个经验非常重要，它揭示了JPA实体生命周期和持久化上下文的核心工作原理。