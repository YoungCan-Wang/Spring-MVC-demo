# 第 4 课：SQL 索引与 EXPLAIN 分析

在之前的课程中，我们学习了如何使用 SQL 进行数据查询和关联。但是，当数据量越来越大时，查询性能就成了我们必须面对的问题。本节课，我们将深入探讨如何通过**索引（Index）**来优化查询，并使用 `EXPLAIN` 命令来分析查询的执行计划。

## 1. 什么是索引？

想象一下，一本厚厚的书，如果没有目录，你要找到某个特定的章节，就只能一页一页地翻。而有了目录，你就可以快速定位到章节所在的页码。

数据库索引就扮演着类似“目录”的角色。它是一种特殊的数据结构，能够让我们快速地查询到数据库表中的特定记录，而无需扫描整张表。

### 索引的优缺点

- **优点**：
  - **提高查询速度**：这是索引最主要的好处。
  - **保证数据唯一性**：通过唯一索引（Unique Index），可以确保表中某列（或几列组合）的值是唯一的。
  - **加速表连接**：在进行 `JOIN` 操作时，如果连接字段上有索引，可以显著提高连接速度。

- **缺点**：
  - **占用存储空间**：索引本身也是数据，需要占用磁盘空间。
  - **降低写操作性能**：当你对表进行 `INSERT`、`UPDATE`、`DELETE` 操作时，数据库不仅要更新数据，还要更新索引，这会带来额外的性能开销。

因此，索引并非越多越好，我们需要根据实际的查询需求来创建最合适的索引。

## 2. `EXPLAIN` 命令

`EXPLAIN` 是 MySQL 提供的一个强大工具，它可以显示 SQL 查询的**执行计划（Execution Plan）**。通过执行计划，我们可以了解 MySQL 是如何处理一条 SQL 语句的，例如：

- 查询的顺序
- 使用了哪些索引
- 是否进行了全表扫描
- 扫描了多少行数据

`EXPLAIN` 的使用非常简单，只需要在 `SELECT` 语句前加上 `EXPLAIN` 关键字即可。

```sql
EXPLAIN SELECT * FROM users WHERE name = 'some_user';
```

### `EXPLAIN` 输出的关键字段

- **`id`**: 查询的唯一标识。
- **`select_type`**: 查询类型（如 `SIMPLE`, `PRIMARY`, `SUBQUERY` 等）。
- **`table`**: 查询的表名。
- **`partitions`**: 匹配的分区。
- **`type`**: **[最重要]** 连接类型，表示查询的访问方式。常见的类型从好到坏依次是：`system` > `const` > `eq_ref` > `ref` > `range` > `index` > `ALL`。
  - `const`: 通过主键或唯一索引进行等值查询，最多只会找到一条记录。
  - `ref`: 通过普通索引进行等值查询。
  - `range`: 在索引上进行范围查询。
  - `index`: 扫描整个索引树。
  - `ALL`: **全表扫描（Full Table Scan）**，性能最差。
- **`possible_keys`**: 可能使用的索引。
- **`key`**: **[最重要]** 实际使用的索引。如果为 `NULL`，表示没有使用索引。
- **`key_len`**: 使用的索引长度。
- **`ref`**: 显示索引的哪一列被使用了。
- **`rows`**: **[重要]** 估算的需要读取的行数。
- **`filtered`**: 按表条件过滤的行百分比。
- **`Extra`**: **[重要]** 额外信息，如 `Using where`, `Using index`, `Using filesort` 等。

我们的优化目标通常是让 `type` 尽可能好，避免出现 `ALL`，并让 `rows` 和 `Extra` 的信息看起来更健康。

---

接下来，我们将在项目中找一个实际的查询场景，用 `EXPLAIN` 来分析并进行索引优化。

## 3. 实战：优化用户查询

在我们的项目中，有一个根据“姓名”和“角色”查询用户的功能。对应的 JPQL 查询如下：

```java
@Query("SELECT u FROM User u WHERE u.name = :name AND u.role = :role")
List<User> findByNameAndRole(@Param("name") String name, @Param("role") String role);
```

对应的 SQL 语句大致是：

```sql
SELECT * FROM users WHERE name = ? AND role = ?;
```

### 3.1. 分析现状

我们回顾一下 `users` 表的结构：

- `id`: 主键索引
- `name`: 唯一索引
- `role`: **无索引**

当我们执行上述查询时，MySQL 会如何选择执行计划呢？

- **理想情况**：MySQL 使用 `uk_name` 索引来快速找到 `name` 匹配的行，然后对这些结果进行二次过滤，检查 `role` 是否匹配。
- **问题**：如果某个 `name` 下的用户数量非常多，即使走了 `name` 索引，后续的 `role` 过滤也会有性能开销。

### 3.2. 优化方案：联合索引

为了让 `name` 和 `role` 的组合查询最高效，我们可以创建一个**联合索引（Composite Index）**。

**联合索引的最佳实践：最左前缀原则（Leftmost Prefix Principle）**

当创建一个像 `(col1, col2, col3)` 这样的联合索引时，查询条件可以有效地使用索引的前缀部分，例如：

- `WHERE col1 = ?` (能用上索引)
- `WHERE col1 = ? AND col2 = ?` (能用上索引)
- `WHERE col1 = ? AND col2 = ? AND col3 = ?` (能用上索引)
- `WHERE col2 = ?` (**不能**用上索引，因为查询没有从索引的最左边列开始)

在我们的场景中，查询条件是 `name` 和 `role`。因此，我们可以创建一个 `(name, role)` 的联合索引。

### 3.3. 创建迁移文件

我们通过 Flyway 创建一个新的 SQL 迁移文件 `V3__Add_name_role_index_to_users.sql` 来添加这个索引。

```sql
-- 为 users 表的 name 和 role 字段添加联合索引
CREATE INDEX `idx_name_role` ON `users` (`name`, `role`);
```

### 3.4. 验证优化效果 (使用 EXPLAIN)

现在，我们可以通过 `EXPLAIN` 来对比优化前后的效果。

**优化前 (假设只使用 `uk_name` 索引)**

```sql
EXPLAIN SELECT * FROM users WHERE name = 'some_name' AND role = 'some_role';
```

你可能会看到类似这样的结果：

- `possible_keys`: `uk_name`
- `key`: `uk_name`
- `Extra`: `Using where` (表示在索引之上还需要进行额外的 `where` 条件过滤)

**优化后 (使用 `idx_name_role` 联合索引)**

```sql
EXPLAIN SELECT * FROM users WHERE name = 'some_name' AND role = 'some_role';
```

理想情况下，你会看到：

- `possible_keys`: `uk_name`, `idx_name_role`
- `key`: `idx_name_role` (MySQL 选择了更高效的联合索引)
- `Extra`: `Using index` 或为空 (表示查询所需的数据可以直接从索引中获取，无需回表，或者没有额外的过滤操作)

通过 `EXPLAIN` 的对比，我们可以清晰地看到，联合索引让查询的执行计划变得更优，从而提升了性能。