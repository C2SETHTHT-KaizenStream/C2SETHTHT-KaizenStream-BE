package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String roleID;
    String name;
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @ManyToMany
    Set<Permission> permissions;
}
