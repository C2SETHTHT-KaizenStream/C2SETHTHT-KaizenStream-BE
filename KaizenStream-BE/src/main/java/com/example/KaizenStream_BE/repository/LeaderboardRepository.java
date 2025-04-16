package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    Optional<Leaderboard> findByUserUserIdAndTypeAndTimeFrameStart(String userId, String type, LocalDate timeFrameStart);
    List<Leaderboard> findTop20ByTypeOrderByTotalDonationsDesc(String type);


}

