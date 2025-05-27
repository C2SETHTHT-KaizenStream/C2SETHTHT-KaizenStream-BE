package com.example.KaizenStream_BE.entity;

import com.example.KaizenStream_BE.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id", referencedColumnName = "livestreamsID", nullable = false)
    private Livestream stream;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;


    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) // Dùng cột 'userID' để ánh xạ với bảng 'users'
    @JsonBackReference(value = "user-reports")
    @ToString.Exclude
    private User user;

    @Column(name = "is_read")
    private boolean isRead = false;
}
