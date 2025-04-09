package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public static Role USER;
    public static Role ADMIN;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "roleID")
    // role_id != roleID
    private String roleId;

    private String name;

    public Role(String roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;

    @ManyToMany
    Set<Permission> permissions;
}
