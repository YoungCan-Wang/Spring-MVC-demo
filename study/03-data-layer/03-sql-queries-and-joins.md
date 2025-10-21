# SQL 查询 (`SELECT` & `JOIN`)

这是从数据库中获取信息最核心的技能。

## 1. 基础 `SELECT` 查询

`SELECT` 用于从数据库表中选取数据，结果被存储在一个结果表中（称为结果集）。

#### 语法

`SELECT column1, column2, ... FROM table_name WHERE condition;`

- **`SELECT ... FROM ...`**: 指定要查询的列和目标表。
- **`WHERE`**: 可选的子句，用于添加筛选条件，过滤出满足条件的行。

#### 示例

- **查询所有列**: 使用 `*` 代表所有列。
  ```sql
  SELECT * FROM users;
  ```

- **查询指定列**: 明确列出列名，性能更好，意图更清晰。
  ```sql
  SELECT id, name, role FROM users;
  ```

- **带条件查询**: 使用 `WHERE` 子句进行筛选。
  ```sql
  -- 查询所有角色为 'tester' 的用户
  SELECT id, name, role FROM users WHERE role = 'tester';

  -- 查询 id 大于 1 的用户
  SELECT * FROM users WHERE id > 1;
  ```

## 2. `INNER JOIN` 关联查询

当我们需要的数据分散在多个表中时（这在规范化的数据库中是常态），就需要用 `JOIN` 将它们连接起来。

`INNER JOIN` 是最常用的一种，它会返回两个表中“连接字段”相匹配的行。

在我们的项目中，`users` 表和 `posts` 表通过 `posts.user_id` = `users.id` 关联。

#### 语法

`SELECT ... FROM table1 INNER JOIN table2 ON table1.column = table2.column;`

- **`ON`**: 指定两个表之间的连接条件。

#### 示例

**需求**：获取所有帖子及其作者的姓名。

```sql
SELECT
    p.id AS post_id, -- 为 p.id 列起一个别名 post_id
    p.title,
    u.name AS author_name -- 为 u.name 列起一个别名 author_name
FROM
    posts p -- 为 posts 表设置别名 p
INNER JOIN
    users u ON p.user_id = u.id; -- 连接条件：帖子的 user_id 必须等于用户的 id
```

- **别名 (Alias)**: 使用 `AS` 关键字可以为表或列创建临时的、更易读的名称，在查询复杂时尤其有用。

