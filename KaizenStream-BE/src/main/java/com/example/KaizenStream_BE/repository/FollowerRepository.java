package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.Follower;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface FollowerRepository extends Repository<Follower,String> {
}
