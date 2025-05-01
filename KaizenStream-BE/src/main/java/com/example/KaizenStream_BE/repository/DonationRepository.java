package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.Donation;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface DonationRepository extends JpaRepository<Donation,String> {
    // Tìm danh sách donation theo user (người tặng)
    List<Donation> findByUser(User user);

    // Tìm danh sách donation theo livestream
    List<Donation> findByLivestream(Livestream livestream);
    List<Donation> findByLivestream_LivestreamId(String livestreamId);
    // Tìm tất cả donation của một người dùng trong một livestream cụ thể
    List<Donation> findByUserAndLivestream(User user, Livestream livestream);

    @Query(value =
            "SELECT " +
                    "    current_month.total_points_spent_current_month, " +
                    "    last_month.total_points_spent_last_month, " +
                    "    CASE " +
                    "        WHEN last_month.total_points_spent_last_month = 0 THEN NULL " +
                    "        ELSE ((current_month.total_points_spent_current_month - last_month.total_points_spent_last_month) / last_month.total_points_spent_last_month) * 100 " +
                    "    END AS growth_percentage " +
                    "FROM " +
                    "    (SELECT SUM(point_spent) AS total_points_spent_current_month " +
                    "     FROM donation " +
                    "     WHERE YEAR(timestamp) = YEAR(GETDATE()) " +
                    "     AND MONTH(timestamp) = MONTH(GETDATE())) AS current_month, " +
                    "    (SELECT SUM(point_spent) AS total_points_spent_last_month " +
                    "     FROM donation " +
                    "     WHERE YEAR(timestamp) = YEAR(GETDATE()) " +
                    "     AND MONTH(timestamp) = MONTH(GETDATE()) - 1) AS last_month",
            nativeQuery = true)
    List<Object[]> getDonationGrowthPercentage();  // Trả về List<Object[]> với các giá trị như total_points_spent_current_month, total_points_spent_last_month, growth_percentage
}
