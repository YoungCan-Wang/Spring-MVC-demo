-- 为 users 表的 name 和 role 字段添加联合索引
CREATE INDEX `idx_name_role` ON `users` (`name`, `role`);
