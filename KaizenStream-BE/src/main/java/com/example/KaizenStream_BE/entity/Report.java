package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @Column(name = "reportID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reportId;

    @Column(columnDefinition = "nvarchar(max)")
    private String reportType;
    private String description;

    @ElementCollection
    @CollectionTable(name = "report_images", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "image_url")
    private List<String> images;

    private LocalDateTime createdAt;

   private String userId;
}
