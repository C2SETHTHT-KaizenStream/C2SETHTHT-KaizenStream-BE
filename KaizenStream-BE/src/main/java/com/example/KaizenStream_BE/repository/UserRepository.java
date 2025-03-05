package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByUserName(String userName);

}
