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



}
