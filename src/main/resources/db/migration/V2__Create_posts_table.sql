-- 创建 posts 表，并与 users 表建立一对多关系
CREATE TABLE `posts` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `user_id` INT NOT NULL COMMENT '外键，关联到 users 表的 id',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
        ON DELETE CASCADE -- 当删除 user 时，该 user 的所有 post 也一并删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
