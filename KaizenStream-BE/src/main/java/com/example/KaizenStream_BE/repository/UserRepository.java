package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User,String> {
}
