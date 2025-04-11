package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.SuggestionRequest;
import com.example.KaizenStream_BE.dto.respone.suggestion.SuggestionResponse;

import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.UserPreferences;
import com.example.KaizenStream_BE.mapper.SuggestionMapper;
import com.example.KaizenStream_BE.repository.LivestreamRepository;
import com.example.KaizenStream_BE.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final LivestreamRepository livestreamRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final SuggestionMapper suggestionMapper;

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestions(SuggestionRequest request) {
        validateRequest(request);
        int limit = adjustLimit(request.getLimit());

        UserPreferences preferences = getUserPreferences(request.getUserId());
        List<Livestream> livestreams = getActiveLivestreams(limit);

        if (isNewUser(preferences)) {
            return getRandomSuggestions(livestreams, limit);
        }

        return getSuggestionsWithScoring(livestreams, preferences, limit);
    }

    private void validateRequest(SuggestionRequest request) {
        if (request == null || request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("Invalid request: User ID is required");
        }
    }

    private int adjustLimit(int limit) {
        return Math.min(Math.max(limit, 1), 50);
    }

    private UserPreferences getUserPreferences(String userId) {
        return userPreferencesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User preferences not found"));
    }

    private List<Livestream> getActiveLivestreams(int limit) {
        return livestreamRepository.findRandomLivestreams(limit);
    }

    private boolean isNewUser(UserPreferences preferences) {
        return preferences.getViewCount() == 0 &&
                preferences.getPreferredTags().isEmpty() &&
                preferences.getPreferredCategories().isEmpty();
    }

    private List<SuggestionResponse> getRandomSuggestions(List<Livestream> livestreams, int limit) {
        Collections.shuffle(livestreams);
        return livestreams.stream()
                .limit(limit)
                .map(suggestionMapper::toSuggestionResponse)
                .collect(Collectors.toList());
    }

    private List<SuggestionResponse> getSuggestionsWithScoring(
            List<Livestream> livestreams,
            UserPreferences preferences,
            int limit) {

        return livestreams.stream()
                .map(livestream -> {
                    double score = calculateScore(livestream, preferences);
                    SuggestionResponse response = suggestionMapper.toSuggestionResponse(livestream);
                    response.setScore(score);
                    return response;
                })
                .sorted(Comparator.comparingDouble(SuggestionResponse::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateScore(Livestream livestream, UserPreferences preferences) {
        double tagScore = 0.0;
        double categoryScore = 0.0;

        Set<String> tagNames = livestream.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet());
        Set<String> categoryNames = livestream.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet());

        for (String preferredTag : preferences.getPreferredTags()) {
            if (tagNames.contains(preferredTag)) {
                tagScore += 1.0;
            }
        }

        for (String preferredCategory : preferences.getPreferredCategories()) {
            if (categoryNames.contains(preferredCategory)) {
                categoryScore += 1.0;
            }
        }

        int viewerCount = livestream.getViewerCount();

        return tagScore * preferences.getTagWeight()
                + categoryScore * preferences.getCategoryWeight()
                + viewerCount * preferences.getViewerCountWeight();
    }
}
