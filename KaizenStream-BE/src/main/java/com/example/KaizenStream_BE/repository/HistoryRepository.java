package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Follower;
import com.example.KaizenStream_BE.entity.History;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface HistoryRepository extends Repository<History,String> {
}
