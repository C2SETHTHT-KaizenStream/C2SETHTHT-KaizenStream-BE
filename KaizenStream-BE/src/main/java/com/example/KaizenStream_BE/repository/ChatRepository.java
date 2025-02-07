package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ChatRepository extends Repository<Chat,String> {
}
