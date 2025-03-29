package com.example.KaizenStream_BE.entity;
import com.example.KaizenStream_BE.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "schedule")
@Data
@Setter
@Getter
public class Schedule {
    @Id
    @Column(name = "scheduleID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String scheduleId;

    private String status= Status.PENDING.getDescription();

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    private Date scheduleTime;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;


}
