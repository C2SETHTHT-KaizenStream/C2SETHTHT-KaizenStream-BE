package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.LivestreamStatus;
import com.example.KaizenStream_BE.mapper.LivestreamMapper;
import com.example.KaizenStream_BE.repository.LivestreamRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LivestreamService {
    LivestreamRepository livestreamRepository;
    LivestreamMapper livestreamMapper;
    UserRepository userRepository;
    public Livestream createLivestream( CreateLivestreamRequest request) {
        User user=userRepository.findById(request.getUserId()).orElseThrow();

        Livestream  livestream= livestreamMapper.toLivestream(request);
        livestream.setUser(user);
        return  livestreamRepository.save(livestream);
    }

    public LivestreamRespone getLivestreamById(String id) {
        return livestreamMapper.toLivestreamRespone(livestreamRepository.findById(id).orElseThrow(()->new RuntimeException("LIVESTREAM_NOT_EXITS")));
    }

    public List<LivestreamRespone> getAll() {
        var live=livestreamRepository.findAll();
        return live.stream().map(l->livestreamMapper.toLivestreamRespone(l)).toList();
    }


    public LivestreamRespone updateLivestreamById( @Valid UpdateLivestreamRequest updateLivestreamRequest) {
        var live=livestreamRepository.findById(updateLivestreamRequest.getLivestreamId()).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        livestreamMapper.updateLivestream(live,updateLivestreamRequest);
        livestreamRepository.save(live);
        return livestreamMapper.toLivestreamRespone(live);
    }

    public String deleteById(String id) {
        var live=livestreamRepository.findById(id).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));

        livestreamRepository.deleteById(id);
        return "Livestream has been deleted";
    }

    public void updateStatus(String streamId, LivestreamStatus status) {
        var live=livestreamRepository.findById(streamId).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setStatus(status.getDescription());
        livestreamRepository.save(live);
    }
}
