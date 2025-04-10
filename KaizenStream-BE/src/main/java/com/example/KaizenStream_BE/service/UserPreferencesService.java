package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.UserPreferences;
import com.example.KaizenStream_BE.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;

    @Transactional
    public UserPreferences createDefaultPreferences(User user) {
        log.info("Creating default preferences for user: {}", user.getUserId());

        return userPreferencesRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> createNewPreferences(user));
    }

    @Transactional
    public void updatePreferences(String userId, Set<String> tags, Set<String> categories) {
        log.info("Updating preferences for user: {}", userId);

        if (tags == null || categories == null) {
            log.warn("Tags or categories are null for user: {}", userId);
            return;
        }

        userPreferencesRepository.findByUser_UserId(userId)
                .ifPresentOrElse(
                        preferences -> updateExistingPreferences(preferences, tags, categories),
                        () -> log.warn("No preferences found for user: {}", userId)
                );
    }

    @Transactional
    public void updateViewCount(String userId) {
        log.info("Updating view count for user: {}", userId);

        userPreferencesRepository.findByUser_UserId(userId)
                .ifPresentOrElse(
                        this::incrementViewCount,
                        () -> log.warn("No preferences found for user: {}", userId)
                );
    }

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

    private void updateExistingPreferences(UserPreferences preferences, Set<String> tags, Set<String> categories) {
        preferences.setPreferredTags(tags);
        preferences.setPreferredCategories(categories);
        preferences.setUpdatedAt(new Date());
        userPreferencesRepository.save(preferences);
    }

    private void incrementViewCount(UserPreferences preferences) {
        preferences.setViewCount(preferences.getViewCount() + 1);
        preferences.setLastViewed(new Date());
        userPreferencesRepository.save(preferences);

    }
}
