package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Report;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ReportRepository extends Repository<Report,String> {
    Report save(Report report);
}
