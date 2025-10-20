package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Lob // @Lob 表示这是一个可以存储大量文本的字段
    private String content;

    // 多对一关系：多个 Post 可以属于一个 User
    @ManyToOne(fetch = FetchType.LAZY) // LAZY 表示延迟加载，只有在真正访问 User 对象时才会去数据库查询
    @JoinColumn(name = "user_id", nullable = false) // 定义外键列的名称为 user_id
    private User user;

    // 构造函数、Getter 和 Setter
    public Post() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
