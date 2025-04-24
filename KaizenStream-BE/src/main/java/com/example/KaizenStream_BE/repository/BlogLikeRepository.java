package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.BlogLike;
import com.example.KaizenStream_BE.entity.BlogLikeId;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogLikeRepository extends JpaRepository<BlogLike, BlogLikeId> {
    void deleteByBlogId(String blogId);
    Optional<BlogLike> findByUserAndBlog(User user, Blog blog);
    List<BlogLike> findByBlog(Blog blog);

}