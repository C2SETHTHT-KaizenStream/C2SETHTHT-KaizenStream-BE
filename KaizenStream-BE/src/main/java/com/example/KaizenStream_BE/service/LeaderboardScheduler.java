package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.Leaderboard;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.LeaderboardRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LeaderboardScheduler {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    private UserRepository userRepository;

    // Chạy mỗi ngày lúc 0h
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDailyLeaderboard() {
        updateLeaderboard("daily", LocalDate.now(), LocalDate.now());
    }

    // Chạy mỗi tuần (chủ nhật 0h)
    @Scheduled(cron = "0 0 0 ? * SUN")
    public void updateWeeklyLeaderboard() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        updateLeaderboard("weekly", startOfWeek, endOfWeek);
    }

    // Chạy mỗi tháng vào ngày 1 lúc 0h
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateMonthlyLeaderboard() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        updateLeaderboard("monthly", startOfMonth, endOfMonth);
    }

    private void updateLeaderboard(String type, LocalDate start, LocalDate end) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            int views = calculateViews(user.getUserId(), start, end);
            BigDecimal donations = calculateDonations(user.getUserId(), start, end);

            Optional<Leaderboard> existing = leaderboardRepository.findByUserUserIdAndTypeAndTimeFrameStart(user.getUserId(), type, start);
            Leaderboard leaderboard = existing.orElse(new Leaderboard());

            leaderboard.setUser(user);
            leaderboard.setTotalViewers(views);
            leaderboard.setTotalDonations(donations);
            leaderboard.setTimeFrameStart(start);
            leaderboard.setTimeFrameEnd(end);
            leaderboard.setType(type);
            leaderboardRepository.save(leaderboard);

        }
    }

    // Giả lập hàm tính views/donations
    private int calculateViews(String userId, LocalDate start, LocalDate end) {
        // Gọi service hoặc repository để tính lượt xem
        return 100; // ví dụ
    }

    private BigDecimal calculateDonations(String userId, LocalDate start, LocalDate end) {
        return new BigDecimal("123.45"); // ví dụ
    }
}
