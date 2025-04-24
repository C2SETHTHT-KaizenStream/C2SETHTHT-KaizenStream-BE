package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ReportRepository extends JpaRepository<Report,String> {
    Report save(Report report);
    List<Report> findByStream_LivestreamId(String livestreamId);
    @Query(value = "SELECT FORMAT(r.created_at, 'MMM') AS month, COUNT(*) AS total_reports " +
            "FROM report r " +
            "WHERE r.status NOT IN ('PENDING', 'REJECT') " +
            "AND YEAR(r.created_at) = YEAR(GETDATE()) " +
            "GROUP BY MONTH(r.created_at), FORMAT(r.created_at, 'MMM') " +
            "ORDER BY MONTH(r.created_at)", nativeQuery = true)
    List<Object[]> findReportCountByMonth();  // Trả về List<Object[]>, mỗi Object[] chứa month và total_reports
}
