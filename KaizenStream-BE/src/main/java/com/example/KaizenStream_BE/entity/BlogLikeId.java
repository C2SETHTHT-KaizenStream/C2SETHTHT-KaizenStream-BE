package com.example.KaizenStream_BE.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogLikeId implements Serializable {
    String userId;
    String blogId;
}
