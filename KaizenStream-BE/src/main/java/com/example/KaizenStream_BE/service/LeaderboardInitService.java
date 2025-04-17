package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.LeaderboardRespone;
import com.example.KaizenStream_BE.entity.Leaderboard;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.ProfileMapper;
import com.example.KaizenStream_BE.repository.LeaderboardRepository;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LeaderboardInitService {


     LeaderboardRepository leaderboardRepository;

     UserRepository userRepository;
     ProfileRepository profileRepository;

    public void initializeLeaderboardForAllUsers() {
        List<User> users = userRepository.findAll();

        LocalDate now = LocalDate.now();
        LocalDate startOfToday = now;
        LocalDate endOfToday = now;

        for (User user : users) {
            for (String type : List.of("daily", "weekly", "monthly")) {
                LocalDate start = switch (type) {
                    case "weekly" -> now.with(DayOfWeek.MONDAY);
                    case "monthly" -> now.withDayOfMonth(1);
                    default -> startOfToday;
                };

                LocalDate end = switch (type) {
                    case "weekly" -> now.with(DayOfWeek.SUNDAY);
                    case "monthly" -> now.withDayOfMonth(now.lengthOfMonth());
                    default -> endOfToday;
                };

                boolean exists = leaderboardRepository
                        .findByUserUserIdAndTypeAndTimeFrameStart(user.getUserId(), type, start)
                        .isPresent();

                if (!exists) {
                    Leaderboard leaderboard = new Leaderboard();
                    leaderboard.setUser(user);
                    leaderboard.setTotalViewers(0);
                    BigDecimal randomDonation = BigDecimal.valueOf(new Random().nextDouble(1000.0)); // 0.0 - 1000.0
                    leaderboard.setTotalDonations(randomDonation.setScale(2, RoundingMode.HALF_UP));
                    leaderboard.setTimeFrameStart(start);
                    leaderboard.setTimeFrameEnd(end);
                    leaderboard.setType(type);

                    leaderboardRepository.save(leaderboard);
                }
            }
        }
    }

    public List<LeaderboardRespone> getTop20ByType(String type) {
        return leaderboardRepository.findTop20ByTypeOrderByTotalViewersDesc(type)
                .stream()
                .map(l -> {
                    String userId = l.getUser().getUserId();

                    String avatarUrl = profileRepository.findByUser_UserId(userId)
                            .map(Profile::getAvatarUrl)
                            .orElse(null);
                    return LeaderboardRespone.builder()
                            .userId(userId)
                            .userName(l.getUser().getUserName())
                            .totalViewers(l.getTotalViewers())
                            .totalDonations(l.getTotalDonations())
                            .type(l.getType())
                            .imgUrl(avatarUrl)
                            .build();
                })
                .toList();
    }

}
