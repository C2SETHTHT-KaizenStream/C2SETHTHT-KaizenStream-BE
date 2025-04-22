package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Page<User> findByChannelNameContainingIgnoreCase(String query, Pageable pageable);

    List<User> findByStatus(AccountStatus accountStatus);

    //Optional<List<User>> findByUserName(String userName);
}
