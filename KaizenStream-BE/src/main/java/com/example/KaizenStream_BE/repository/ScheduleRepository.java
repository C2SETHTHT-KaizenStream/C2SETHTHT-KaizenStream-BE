package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ScheduleRepository extends JpaRepository<Schedule,String> {
}
