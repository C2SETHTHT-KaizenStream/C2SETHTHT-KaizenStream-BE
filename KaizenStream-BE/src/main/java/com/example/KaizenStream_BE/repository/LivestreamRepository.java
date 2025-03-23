package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Follower;
import com.example.KaizenStream_BE.entity.Livestream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface LivestreamRepository extends JpaRepository<Livestream,String> {

}
