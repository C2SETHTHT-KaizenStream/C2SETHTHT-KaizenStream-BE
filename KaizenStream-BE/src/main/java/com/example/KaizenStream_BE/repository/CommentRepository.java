package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface CommentRepository extends JpaRepository<Comment,String> {

    List<Comment> findByBlog_BlogId(String blogID);
}
