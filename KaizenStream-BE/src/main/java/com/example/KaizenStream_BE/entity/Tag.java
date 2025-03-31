package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "tag")

public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tagId;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Livestream> livestreams = new HashSet<>();





}
