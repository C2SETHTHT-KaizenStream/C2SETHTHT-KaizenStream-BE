package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Livestream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivestreamRepository extends JpaRepository<Livestream, String> {
    @Query(value = "SELECT * FROM livestreams WHERE status = 'ACTIVE' ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Livestream> findRandomLivestreams(int limit);

    @Query("SELECT l FROM Livestream l WHERE l.status = 'ACTIVE' ORDER BY l.viewerCount DESC")
    List<Livestream> findTopLivestreamsByViewerCount();
}
