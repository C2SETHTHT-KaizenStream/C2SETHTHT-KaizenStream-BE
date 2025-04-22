package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Livestream;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Repository
public interface LivestreamRepository extends JpaRepository<Livestream, String> {
    @Query(value = "SELECT * FROM livestreams WHERE status = 'ACTIVE' ORDER BY NEWID() OFFSET 0 ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Livestream> findRandomLivestreams(@Param("limit") int limit);





    @Query("SELECT l FROM Livestream l WHERE l.status = 'ACTIVE' ORDER BY l.viewerCount DESC")
    List<Livestream> findTopLivestreamsByViewerCount();

    Page<Livestream> findByUser_UserId(String userId, Pageable pageable);
    Page<Livestream> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Livestream> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    @Query(value =
            "SELECT FORMAT(DATEADD(MONTH, MONTH(start_time)-1, '2025-01-01'), 'MMM') AS month, " +
                    "SUM(viewer_count) AS total_view_count " +
                    "FROM livestreams " +
                    "WHERE YEAR(start_time) = YEAR(GETDATE()) " +
                    "GROUP BY MONTH(start_time) " +
                    "ORDER BY MONTH(start_time)",
            nativeQuery = true)
    List<Object[]> getMonthlyViewerCounts();

    @Query(value =
            "SELECT TOP 4 " +
                    "u.userID, " +
                    "u.user_name, " +
                    "p.avatar_url, " +
                    "SUM(l.viewer_count) AS total_view_count " +
                    "FROM livestreams l " +
                    "JOIN users u ON l.userID = u.userID " +
                    "JOIN profiles p ON p.userID = u.userID " +
                    "WHERE YEAR(l.start_time) = YEAR(GETDATE()) " +   // Lọc các livestreams trong năm hiện tại
                    "GROUP BY u.user_name, u.userID, p.avatar_url " +  // Nhóm theo người dùng (userID)
                    "ORDER BY total_view_count DESC",
            nativeQuery = true)
    List<Object[]> getTopUsersByViewCount();

}
