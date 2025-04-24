package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_preferred_tags", joinColumns = @JoinColumn(name = "preferences_id"))
    @Column(name = "tag")
    private Set<String> preferredTags;

    @ElementCollection
    @CollectionTable(name = "user_preferred_categories", joinColumns = @JoinColumn(name = "preferences_id"))
    @Column(name = "category")
    private Set<String> preferredCategories;

    @NotNull(message = "Viewer count weight cannot be null")
    @Column(nullable = false)
    @Builder.Default
    private Double viewerCountWeight = 0.3;

    @NotNull(message = "Tag weight cannot be null")
    @Column(nullable = false)
    @Builder.Default
    private Double tagWeight = 0.4;

    @NotNull(message = "Category weight cannot be null")
    @Column(nullable = false)
    @Builder.Default
    private Double categoryWeight = 0.3;

    @NotNull(message = "View count cannot be null")
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;  // fix null triệt để

    @Column(name = "last_viewed")
    private Date lastViewed;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        // phòng khi data lỗi null vẫn force về 0
        if (viewCount == null) {
            viewCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        if (viewCount == null) {
            viewCount = 0;
        }
    }
}
