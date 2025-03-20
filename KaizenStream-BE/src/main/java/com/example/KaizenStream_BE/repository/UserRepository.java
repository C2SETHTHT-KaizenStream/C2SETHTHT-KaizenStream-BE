package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);
    //Optional<List<User>> findByUserName(String userName);
}
