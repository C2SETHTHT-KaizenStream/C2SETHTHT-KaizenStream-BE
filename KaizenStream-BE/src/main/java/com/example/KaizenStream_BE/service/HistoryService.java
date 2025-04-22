package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.History;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.HistoryRepository;
import com.example.KaizenStream_BE.repository.LivestreamRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final LivestreamRepository livestreamRepository;

    public void saveViewHistory(String userId, String livestreamId, int durationSeconds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Livestream livestream = livestreamRepository.findById(livestreamId)
                .orElseThrow(() -> new RuntimeException("Livestream not found"));

      History history = new History();
      history.setUser(user );
      history.setLivestream(livestream);
      history.setActionTime(new Date());
      history.setWatchDuration(0);
        historyRepository.save(history);
    }
}
