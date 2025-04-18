package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.request.livestream.LivestreamRedisData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
@Service
@RequiredArgsConstructor
public class LivestreamRedisService {

    private final RedisTemplate<String, Object> livestreamRedisTemplate;
    private final ObjectMapper objectMapper;

    private String buildKey(String livestreamId) {
        return "livestream:" + livestreamId;
    }

    private LivestreamRedisData safeGet(String key) {
        Object raw = livestreamRedisTemplate.opsForValue().get(key);
        if (raw == null) return new LivestreamRedisData();

        if (raw instanceof LinkedHashMap<?, ?>) {
            return objectMapper.convertValue(raw, LivestreamRedisData.class);
        } else if (raw instanceof LivestreamRedisData) {
            return (LivestreamRedisData) raw;
        }
        return new LivestreamRedisData(); // fallback
    }

    public void saveOrUpdateViewCounts(String livestreamId, int count) {
        String key = buildKey(livestreamId);
        LivestreamRedisData data = safeGet(key);
        data.setViewCount(count);
        livestreamRedisTemplate.opsForValue().set(key, data);
    }

    public void saveOrUpdateDuration(String livestreamId, int duration) {
        String key = buildKey(livestreamId);
        LivestreamRedisData data = safeGet(key);
        data.setDuration(duration);
        livestreamRedisTemplate.opsForValue().set(key, data);
    }

    public LivestreamRedisData getData(String livestreamId) {
        return safeGet(buildKey(livestreamId));
    }
}
