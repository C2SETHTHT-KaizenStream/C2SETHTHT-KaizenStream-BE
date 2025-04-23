package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.History;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.UserPreferences;
import com.example.KaizenStream_BE.repository.HistoryRepository;
import com.example.KaizenStream_BE.repository.UserPreferencesRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    // Tạo preferences mặc định cho user mới
    @Transactional
    public UserPreferences createDefaultPreferences(User user) {
        log.info("Creating default preferences for user: {}", user.getUserId());

        return userPreferencesRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> createNewPreferences(user));
    }

    // Cập nhật preferences thủ công (tags, categories truyền vào)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserPreferences updatePreferences(String userId, Set<String> tags, Set<String> categories) {
        log.info("Updating preferences for user: {}", userId);

        if (tags == null || categories == null) {
            log.warn("Tags or categories are null for user: {}", userId);
            return null;
        }

        // Kiểm tra nếu preferences đã tồn tại và cập nhật chúng
        return userPreferencesRepository.findByUser_UserId(userId)
                .map(preferences -> {
                    log.info("Update preferences");
                    preferences.setPreferredTags(tags);  // Cập nhật tags
                    preferences.setPreferredCategories(categories);  // Cập nhật categories
                    preferences.setUpdatedAt(new Date());  // Cập nhật thời gian cập nhật
                    return userPreferencesRepository.save(preferences);  // Lưu lại sau khi cập nhật
                })
                .orElseGet(() -> {
                    log.warn("No preferences found for user: {}", userId);
                    // Lấy đối tượng User từ userId
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    // Tạo mới UserPreferences nếu không tồn tại
                    UserPreferences newPreferences = createNewPreferences(user, tags, categories);
                    log.info("Create done");
                    return userPreferencesRepository.save(newPreferences);  // Lưu lại preferences mới
                });
    }

    // Cập nhật preferences tự động từ history xem nhiều nhất
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserPreferences updatePreferencesFromHistory(String userId) {
        log.info("Updating preferences from history for user: {}", userId);

        // Lấy tất cả lịch sử của người dùng
        List<History> historyList = historyRepository.findByUser_UserId(userId);

        // Tạo các Set để lưu tags và categories
        Set<String> tags = new HashSet<>();
        Set<String> categories = new HashSet<>();

        // Lấy tags và categories từ mỗi livestream trong lịch sử
        for (History history : historyList) {
            Livestream livestream = history.getLivestream(); // Lấy livestream từ history

            if (livestream != null) {
                // Thêm tất cả tags và categories của livestream vào Set
                tags.addAll(livestream.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet()));
                categories.addAll(livestream.getCategories().stream().map(category -> category.getName()).collect(Collectors.toSet()));
            }
        }

        // Cập nhật sở thích người dùng với tags và categories từ lịch sử
        return updatePreferences(userId, tags, categories);  // Gọi phương thức updatePreferences để lưu vào UserPreferences
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
                .user(user)  // Liên kết với User
                .preferredTags(Set.of())  // Tags mặc định là set trống
                .preferredCategories(Set.of())  // Categories mặc định là set trống
                .viewCount(0)
                .lastViewed(new Date())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return userPreferencesRepository.save(preferences);  // Lưu vào cơ sở dữ liệu
    }

    // Tạo mới UserPreferences nếu không tồn tại
    private UserPreferences createNewPreferences(User user, Set<String> tags, Set<String> categories) {
        UserPreferences preferences = UserPreferences.builder()
                .user(user)
                .preferredTags(tags != null ? tags : Set.of())  // Nếu tags null, tạo set trống
                .preferredCategories(categories != null ? categories : Set.of())  // Nếu categories null, tạo set trống
                .viewCount(0)
                .lastViewed(new Date())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return userPreferencesRepository.save(preferences);  // Lưu vào cơ sở dữ liệu
    }
}

