# MySQL 学习笔记：从表结构到索引优化

本笔记将依据学习计划，系统性地整理 MySQL 相关的核心知识。

## 1. 表结构 (Table Structure)

在数据库设计中，表结构是数据的骨架。一个好的表结构能够确保数据的完整性、一致性，并为高效的查询奠定基础。

### 1.1 关键概念

#### 1.1.1 主键 (Primary Key)

- **定义**: 表中每一行数据的唯一标识符。
- **特性**:
    - **唯一性 (Uniqueness)**: 主键的值在表中必须是唯一的，不能重复。
    - **非空性 (Not Null)**: 主键的值不能为空 (`NULL`)。
- **作用**: 确保可以准确地定位到表中的任意一条记录。
- **示例**: 在 `users` 表中，`id` 字段通常被设为主键。

#### 1.1.2 自增 (Auto Increment)

- **定义**: 通常与主键配合使用，当插入新记录时，数据库会自动为该字段生成一个唯一的、递增的数值。
- **作用**: 简化了主键的管理，开发者无需手动分配唯一的 ID。
- **示例**: `id BIGINT PRIMARY KEY AUTO_INCREMENT`

#### 1.1.3 外键 (Foreign Key)

- **定义**: 一个表中的字段，其值指向另一个表的主键。
- **作用**: 用于建立和加强两个表数据之间的链接，实现数据的**参照完整性**。如果表 A 的一个字段是表 B 的外键，那么这个字段的每一个值都必须在表
  B 的主键中存在。
- **示例**: 假设我们有一个 `orders` 表，其中 `user_id` 字段可以作为外键，指向 `users` 表的 `id` 主键，表示这个订单属于哪个用户。

#### 1.1.4 索引 (Index)

- **定义**: 一种特殊的数据结构，可以帮助数据库系统高效地查询数据。它类似于书籍的目录，可以让你快速找到所需内容，而无需逐页翻阅。
- **作用**:
    - **加速查询**: 大大提高 `SELECT` 查询的速度。
    - **保证唯一性**: 唯一索引可以确保列中的所有值都是唯一的。
- **缺点**:
    - **占用空间**: 索引需要额外的磁盘空间。
    - **降低写操作速度**: 当对表中的数据进行 `INSERT`, `UPDATE`, `DELETE` 操作时，索引也需要动态地维护，会带来性能开销。
- **常见类型**: 普通索引、唯一索引、主键索引、组合索引等。

#### 1.1.5 字段类型 (Field Types)

- **定义**: 定义了列中可以存储的数据的类型。
- **作用**:
    - **数据约束**: 确保只存储有效的数据（例如，日期列只存日期）。
    - **空间优化**: 选择最合适的字段类型可以节省存储空间。
- **常见类型**:
    - **数值类型**: `INT`, `BIGINT`, `TINYINT`, `FLOAT`, `DOUBLE`, `DECIMAL`
    - **字符串类型**: `VARCHAR`, `CHAR`, `TEXT`
    - **日期/时间类型**: `DATE`, `TIME`, `DATETIME`, `TIMESTAMP`
    - **布尔类型**: 通常用 `TINYINT(1)` 表示。

### 1.2 实践：为 User 类设计表结构

现在，我们将上述概念应用于当前项目中的 `User` 类。`User.java` 的核心字段是 `id`, `name`, 和 `role`。

下面是为其设计的 SQL `CREATE TABLE` 语句：

```sql
-- 为 User 类设计的 users 表
CREATE TABLE `users` (
    -- id: 整数类型，作为主键，并且在新纪录插入时自动递增。
    `id` INT NOT NULL AUTO_INCREMENT,

    -- name: 字符串类型，最大长度为 50，不能为空。
    -- 我们在这里添加一个唯一索引，确保用户名不重复。
    `name` VARCHAR(50) NOT NULL,

    -- role: 字符串类型，最大长度为 20，可以为空。
    `role` VARCHAR(20),

    -- 定义主键
    PRIMARY KEY (`id`),

    -- 添加唯一索引来保证 name 字段的唯一性
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 设计思路解释:

1. **`id` (INT, NOT NULL, AUTO_INCREMENT, PRIMARY KEY)**: 对应 `User` 中的 `Integer id`。这是典型的代理主键设计，使用自增整数，简单高效。
2. **`name` (VARCHAR(50), NOT NULL, UNIQUE)**: 对应 `String name`。`VARCHAR(50)` 是一个合理的长度，`NOT NULL` 保证了用户名的存在，
   `UNIQUE KEY` 约束保证了用户名的唯一性，这在用户系统中通常是必需的。
3. **`role` (VARCHAR(20))**: 对应 `String role`。我们假设角色字段不是必需的，所以没有加 `NOT NULL`。

## 2. 关系设计 (Relationship Design)

当应用变得复杂时，我们不能把所有信息都塞在一张大表里。我们需要将数据按领域（实体）拆分到不同的表中，并通过关系将它们联系起来。这可以减少数据冗余，保证数据的一致性。

### 2.1 一对多 (One-to-Many)

这是最常见的关系。

- **定义**: A 表中的一条记录可以对应 B 表中的多条记录，但 B 表中的一条记录只能对应 A 表中的一条记录。
- **例子**: 一个**用户**可以发表多篇**文章**，但一篇文章只能属于一个用户。
- **实现**: 在“多”的一方（`posts` 表）添加一个外键（`user_id`）指向“一”的一方（`users` 表）的主键。

```sql
-- posts 表会有一个 user_id 字段
CREATE TABLE `posts` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `user_id` INT NOT NULL, -- 外键
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) -- 建立外键约束
);
```

### 2.2 多对多 (Many-to-Many)

- **定义**: A 表中的一条记录可以对应 B 表中的多条记录，同时 B 表中的一条记录也可以对应 A 表中的多条记录。
- **例子**: 一篇**文章**可以有多个**标签**（Tag），一个**标签**也可以用在多篇文章上。
- **实现**: 需要一个第三方的“中间表”（或称为连接表），来记录 A 和 B 之间的关系。

```sql
-- 假设存在一个 tags 表
CREATE TABLE `tags` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
);

-- 中间表 post_tags
CREATE TABLE `post_tags` (
    `post_id` INT NOT NULL,
    `tag_id` INT NOT NULL,
    PRIMARY KEY (`post_id`, `tag_id`), -- 复合主键
    FOREIGN KEY (`post_id`) REFERENCES `posts`(`id`),
    FOREIGN KEY (`tag_id`) REFERENCES `tags`(`id`)
);
```

### 2.3 一对一 (One-to-One)

- **定义**: A 表中的一条记录只能对应 B 表中的一条记录，反之亦然。
- **例子**: 一个**用户**只有一个**用户配置**（`user_profile`）。
- **实现**: 在任意一方添加外键，并为该外键添加**唯一约束 (UNIQUE)**。通常会把不常用的、或者可以为空的字段拆分出去作为一对一关系。

```sql
CREATE TABLE `user_profiles` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `bio` VARCHAR(255),
    `website` VARCHAR(255),
    `user_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`), -- 唯一约束是关键
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);
```

---
