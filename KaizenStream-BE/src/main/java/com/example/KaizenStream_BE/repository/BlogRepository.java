package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, String> {



    Page<Blog> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Blog> findByContentContainingIgnoreCase(String content, Pageable pageable);

    // find by users
    Page<Blog> findByUser_UserId(String userId, Pageable pageable);
    List<Blog> findByUser_UserId(String userId);
}