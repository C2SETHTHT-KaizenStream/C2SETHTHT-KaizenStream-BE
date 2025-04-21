package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.BlogLike;
import com.example.KaizenStream_BE.entity.BlogLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogLikeRepository extends JpaRepository<BlogLike, BlogLikeId> {
    boolean existsByUserIdAndBlogId(String userId, String blogId);

    void deleteByUserIdAndBlogId(String userId, String blogId);
    void deleteByBlogId(String blogId);

}