package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByUser_UserId(String userId);
    boolean existsByUser_UserId(String userId);
} 