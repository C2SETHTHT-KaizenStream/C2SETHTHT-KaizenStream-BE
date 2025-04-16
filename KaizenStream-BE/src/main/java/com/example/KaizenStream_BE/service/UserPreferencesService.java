package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.UserPreferences;
import com.example.KaizenStream_BE.repository.HistoryRepository;
import com.example.KaizenStream_BE.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final HistoryRepository historyRepository;

    // Tạo preferences mặc định cho user mới
    @Transactional
    public UserPreferences createDefaultPreferences(User user) {
        log.info("Creating default preferences for user: {}", user.getUserId());

        return userPreferencesRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> createNewPreferences(user));
    }

    // Update preferences thủ công (tags, categories truyền vào)
    @Transactional
    public void updatePreferences(String userId, Set<String> tags, Set<String> categories) {
        log.info("Updating preferences for user: {}", userId);

        if (tags == null || categories == null) {
            log.warn("Tags or categories are null for user: {}", userId);
            return;
        }

        userPreferencesRepository.findByUser_UserId(userId)
                .ifPresentOrElse(
                        preferences -> {
                            preferences.setPreferredTags(tags);
                            preferences.setPreferredCategories(categories);
                            preferences.setUpdatedAt(new Date());
                            userPreferencesRepository.save(preferences);
                        },
                        () -> log.warn("No preferences found for user: {}", userId)
                );
    }

    // Update preferences tự động từ history xem nhiều nhất
    @Transactional
    public void updatePreferencesFromHistory(String userId) {
        log.info("Updating preferences from history for user: {}", userId);

        List<Object[]> topTags = historyRepository.findMostViewedTagsByUserId(userId, PageRequest.of(0, 10));
        List<Object[]> topCategories = historyRepository.findMostViewedCategoriesByUserId(userId, PageRequest.of(0, 10));

        Set<String> tags = topTags.stream().map(o -> (String) o[0]).collect(Collectors.toSet());
        Set<String> categories = topCategories.stream().map(o -> (String) o[0]).collect(Collectors.toSet());

        updatePreferences(userId, tags, categories);
    }

    // Update viewCount mỗi lần user xem livestream
    @Transactional
    public void updateViewCount(String userId) {
        log.info("Updating view count for user: {}", userId);

        userPreferencesRepository.findByUser_UserId(userId)
                .ifPresentOrElse(
                        preferences -> {
                            preferences.setViewCount(preferences.getViewCount() + 1);
                            preferences.setLastViewed(new Date());
                            userPreferencesRepository.save(preferences);
                        },
                        () -> log.warn("No preferences found for user: {}", userId)
                );
    }

    // Private tạo mới preferences khi user chưa có
    private UserPreferences createNewPreferences(User user) {
        UserPreferences preferences = UserPreferences.builder()
                .user(user)
                .preferredTags(Set.of())
                .preferredCategories(Set.of())
                .viewCount(0)
                .lastViewed(new Date())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return userPreferencesRepository.save(preferences);
    }
}
