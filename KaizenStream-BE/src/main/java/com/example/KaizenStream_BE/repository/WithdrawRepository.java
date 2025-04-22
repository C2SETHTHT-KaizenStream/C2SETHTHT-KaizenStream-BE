package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawRepository extends JpaRepository<Withdraw, String> {
    List<Withdraw> findByUserOrderByCreatedAtDesc(User user);
}
