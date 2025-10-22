package com.example.demo;

import com.example.demo.dto.PostAuthorDto;

import com.example.demo.dto.RoleCountDto;

import com.example.demo.model.Post;

import com.example.demo.model.User;

import com.example.demo.repository.PostRepository;

import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;



import java.util.Arrays;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;



import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;



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

	@Transactional // 使用 @Transactional 来保持 session 打开，以便在断言时能懒加载 posts 集合

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



	@Test

	@Transactional

	void testFindPostsWithAuthorNames() {

		// 1. 准备测试数据

		User author = new User();

		String authorName = "author_" + System.currentTimeMillis();

		author.setName(authorName);

		author.setRole("author");



		Post post = new Post();

		post.setTitle("A Post for JPQL Test");

		post.setUser(author);

		author.getPosts().add(post);

		userRepository.save(author);



		// 2. 调用我们自定义的查询方法

		List<PostAuthorDto> results = postRepository.findPostsWithAuthorNames();



		// 3. 断言结果

		assertNotNull(results);

		// 确认查询结果中至少有一条是我们刚刚插入的数据

		boolean found = results.stream().anyMatch(

				dto -> dto.postTitle().equals("A Post for JPQL Test") &&

					   dto.authorName().equals(authorName)

		);

		assertEquals(true, found, "The newly created post and author should be found");

	}



	@Test

	@Transactional

	void testCountUsersByRole() {

		// 1. 准备测试数据：2个developer, 1个tester

		userRepository.saveAll(Arrays.asList(

				new User(null, "dev1", "developer"),

				new User(null, "dev2", "developer"),

				new User(null, "tester1", "tester")

		));



		// 2. 调用统计方法

		List<RoleCountDto> counts = userRepository.countUsersByRole();



		// 3. 断言结果

		assertNotNull(counts);

		RoleCountDto devCount = counts.stream().filter(c -> c.role().equals("developer")).findFirst().orElse(null);

		RoleCountDto testerCount = counts.stream().filter(c -> c.role().equals("tester")).findFirst().orElse(null);



		assertNotNull(devCount);

		assertNotNull(testerCount);

		assertEquals(2, devCount.userCount());

		assertEquals(1, testerCount.userCount());

	}



	@Test

	@Transactional

	void testCountUsersByRoleHavingMinCount() {

		// 1. 准备测试数据：3个developer, 2个tester, 1个admin

		userRepository.saveAll(Arrays.asList(

				new User(null, "dev_a", "developer"),

				new User(null, "dev_b", "developer"),

				new User(null, "dev_c", "developer"),

				new User(null, "test_a", "tester"),

				new User(null, "test_b", "tester"),

				new User(null, "admin_a", "admin")

		));



		// 2. 调用方法，minCount = 2

		List<RoleCountDto> countsMin2 = userRepository.countUsersByRoleHavingMinCount(2);

		assertNotNull(countsMin2);

		assertEquals(2, countsMin2.size()); // 应该返回 developer 和 tester

		Map<String, Long> mapMin2 = countsMin2.stream()

				.collect(Collectors.toMap(RoleCountDto::role, RoleCountDto::userCount));

		assertEquals(3L, mapMin2.get("developer"));

		assertEquals(2L, mapMin2.get("tester"));



		// 3. 调用方法，minCount = 3

		List<RoleCountDto> countsMin3 = userRepository.countUsersByRoleHavingMinCount(3);

		assertNotNull(countsMin3);

		assertEquals(1, countsMin3.size()); // 应该只返回 developer

		assertEquals("developer", countsMin3.get(0).role());

		assertEquals(3L, countsMin3.get(0).userCount());



		// 4. 调用方法，minCount = 4

		List<RoleCountDto> countsMin4 = userRepository.countUsersByRoleHavingMinCount(4);

		assertNotNull(countsMin4);

		assertTrue(countsMin4.isEmpty()); // 应该返回空列表

	}



}




