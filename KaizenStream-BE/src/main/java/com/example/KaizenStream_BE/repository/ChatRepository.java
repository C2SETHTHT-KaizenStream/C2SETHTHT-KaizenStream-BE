package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ChatRepository extends JpaRepository<Chat,String> {

    //Lay tin nhan theo livestream id
//    List<Chat> findByLivestream_LivestreamId(String livestreamId);
    Page<Chat> findByLivestream_LivestreamId(String livestreamId, Pageable pageable);

}
