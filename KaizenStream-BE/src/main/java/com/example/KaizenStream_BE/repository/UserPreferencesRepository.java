package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {

    @Query("SELECT up FROM UserPreferences up " +
            "LEFT JOIN FETCH up.preferredTags " +
            "LEFT JOIN FETCH up.preferredCategories " +
            "WHERE up.user.userId = :userId")
    Optional<UserPreferences> findUserPreferencesWithTagsAndCategories(String userId);

    Optional<UserPreferences> findByUser_UserId(String userId);
}
