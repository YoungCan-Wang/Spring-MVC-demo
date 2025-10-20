# 笔记：本地 MySQL 环境搭建指南 (macOS)

本笔记记录在 macOS 系统上，通过 Homebrew 安装和配置 MySQL 数据库的完整流程，作为项目开发的基础环境准备。

### 核心工具

- **Homebrew**: macOS 的包管理器，用于简化软件的安装和管理。

### 步骤 1：安装 Homebrew (如果尚未安装)

检查是否已安装：

```bash
brew --version
```

如果未安装，执行官方命令进行安装：

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 步骤 2：使用 Homebrew 安装 MySQL

```bash
brew install mysql
```

### 步骤 3：启动 MySQL 服务

将 MySQL 设置为开机自启并立即启动。

```bash
brew services start mysql
```

### 步骤 4：安全设置并设置密码

运行 MySQL 的安全设置脚本，这是**非常关键**的一步。

```bash
mysql_secure_installation
```

在脚本的交互式提问中：

1. 设置 `root` 用户的密码，并牢记。
2. 对于接下来的所有问题（“移除匿名用户？”、“禁止 root 远程登录？”等），全部回答“是”（输入 `y`）即可。

### 步骤 5：创建项目所需的数据库

1. 使用 `root` 用户登录到 MySQL 命令行：
   ```bash
   mysql -u root -p
   ```
   (在提示符下输入您在第 4 步设置的密码)

2. 在 `mysql>` 提示符下，创建一个供项目使用的数据库（例如 `demo_db`）：
   ```sql
   CREATE DATABASE demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. 输入 `exit;` 退出 MySQL 命令行。

### 步骤 6：更新 Spring Boot 项目配置

修改 `src/main/resources/application.yml` 文件，使其连接到刚刚创建的数据库。

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/demo_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  username: root
  password: # 您在第 4 步中设置的 root 密码
```
