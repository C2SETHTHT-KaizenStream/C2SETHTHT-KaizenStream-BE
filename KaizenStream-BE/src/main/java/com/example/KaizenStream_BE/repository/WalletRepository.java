package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUser(User user);
}
