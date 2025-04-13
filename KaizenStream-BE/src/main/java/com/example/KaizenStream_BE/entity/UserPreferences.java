package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;  // Sử dụng giá trị mặc định là 0 nếu không có giá trị

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
