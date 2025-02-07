package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Comment;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface CommentRepository extends Repository<Comment,String> {
}
