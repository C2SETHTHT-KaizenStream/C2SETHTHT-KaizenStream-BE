package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String categoryId;

    @Column(nullable = false)
    String name;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    String description;

    @ManyToMany(mappedBy = "categories")
    private List<Livestream> livestreams;
}
