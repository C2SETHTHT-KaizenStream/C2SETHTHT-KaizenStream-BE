package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.History;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface HistoryRepository extends JpaRepository<History, String> {
    // Tìm lịch sử theo user và hành động
    List<History> findByUser_UserIdAndAction(String userId, String action);

    // Tìm lịch sử theo user và hành động, sắp xếp theo thời gian giảm dần và giới hạn số lượng
    List<History> findByUser_UserIdAndActionOrderByActionTimeDesc(String userId, String action, Pageable pageable);

    // Đếm số lần xem theo livestream
    @Query("SELECT COUNT(h) FROM History h WHERE h.user.userId = :userId AND h.action = 'VIEW' AND h.livestream.livestreamId = :livestreamId")
    Long countByUserIdAndLivestreamId(@Param("userId") String userId, @Param("livestreamId") String livestreamId);

    // Lấy lịch sử xem trong khoảng thời gian
    List<History> findByUser_UserIdAndActionAndActionTimeBetween(
            String userId, String action, Date startDate, Date endDate);

    // Query để lấy danh sách các tag phổ biến mà người dùng đã xem
    @Query("SELECT t.name, COUNT(h) as count FROM History h " +
            "JOIN h.livestream l " +
            "JOIN l.tags t " +
            "WHERE h.user.userId = :userId AND h.action = 'VIEW' " +
            "GROUP BY t.name " +
            "ORDER BY count DESC")
    List<Object[]> findMostViewedTagsByUserId(@Param("userId") String userId, Pageable pageable);

    // Query để lấy danh sách các category phổ biến mà người dùng đã xem
    @Query("SELECT c.name, COUNT(h) as count FROM History h " +
            "JOIN h.livestream l " +
            "JOIN l.categories c " +
            "WHERE h.user.userId = :userId AND h.action = 'VIEW' " +
            "GROUP BY c.name " +
            "ORDER BY count DESC")
    List<Object[]> findMostViewedCategoriesByUserId(@Param("userId") String userId, Pageable pageable);

    // Xóa lịch sử xem cũ
    void deleteByActionAndActionTimeBefore(String action, Date date);

    // Lấy lịch sử xem gần đây nhất
    History findFirstByUser_UserIdAndActionOrderByActionTimeDesc(String userId, String action);


    List<History> findByUser_UserId(String userId);
}