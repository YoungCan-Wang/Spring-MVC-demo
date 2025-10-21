package com.example.demo.repository;

import com.example.demo.dto.PostAuthorDto;
import com.example.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT new com.example.demo.dto.PostAuthorDto(p.title, u.name) FROM Post p JOIN p.user u")
    List<PostAuthorDto> findPostsWithAuthorNames();
}
