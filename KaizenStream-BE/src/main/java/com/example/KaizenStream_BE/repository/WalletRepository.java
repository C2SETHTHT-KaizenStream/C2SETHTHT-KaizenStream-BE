package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUser(User user);

    Optional<Wallet> findByUser_UserId(String userUserId);

    // Method có Lock để đảm bảo không bị truy cập đồng thời
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user = :user")
    Optional<Wallet> findByUserForUpdate(@Param("user") User user);
}
