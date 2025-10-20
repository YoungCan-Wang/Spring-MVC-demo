package com.example.demo;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

	@Test
	void contextLoads() {
	}

    @Test
    @Transactional
        // 使用 @Transactional 来保持 session 打开，以便在断言时能懒加载 posts 集合
    void testUserPostRelationship() {
        // 1. 创建一个新的 User
        User user = new User();
        user.setName("test_user_" + System.currentTimeMillis());
        user.setRole("tester");

        // 2. 创建两个 Post
        Post post1 = new Post();
        post1.setTitle("My First Post");
        post1.setContent("Content of the first post.");
        post1.setUser(user); // 建立从 Post 到 User 的关联

        Post post2 = new Post();
        post2.setTitle("My Second Post");
        post2.setContent("Content of the second post.");
        post2.setUser(user); // 建立从 Post 到 User 的关联

        // 3. 将 Post 添加到 User 的集合中（维护双向关系）
        user.getPosts().add(post1);
        user.getPosts().add(post2);

        // 4. 保存 User（由于 CascadeType.ALL，Post 也会被一并保存）
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        // 5. 从数据库中重新获取 User
        User fetchedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(fetchedUser);

        // 6. 断言关系是否正确
        assertEquals(2, fetchedUser.getPosts().size());
        assertEquals("My First Post", fetchedUser.getPosts().get(0).getTitle());
    }

}
