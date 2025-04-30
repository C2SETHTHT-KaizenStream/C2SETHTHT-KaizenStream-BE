package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.request.livestream.LivestreamRedisData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LivestreamRedisService {

    private final RedisTemplate<String, Object> livestreamRedisTemplate;
    private final ObjectMapper objectMapper;

    private String buildKey(String livestreamId, boolean isLive ) {
        if(isLive) return "livestream:" + livestreamId;
        else return "vod:viewCount:"+livestreamId;
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

    public void saveOrUpdateViewCounts(String livestreamId, int count,boolean isLive) {
        String key = buildKey(livestreamId,isLive);
        LivestreamRedisData data = safeGet(key);
        data.setViewCount(count);
        livestreamRedisTemplate.opsForValue().set(key, data);
    }

    public void saveOrUpdateDuration(String livestreamId, int duration,boolean isLive) {
        String key = buildKey(livestreamId,isLive);
        LivestreamRedisData data = safeGet(key);
        data.setDuration(duration);
        livestreamRedisTemplate.opsForValue().set(key, data);
    }

    public LivestreamRedisData getData(String livestreamId,boolean isLive) {
        return safeGet(buildKey(livestreamId,isLive));
    }
    public void removeData(String livestreamId,boolean isLive) {
        String key=buildKey(livestreamId,isLive);
        if(livestreamRedisTemplate.hasKey(key))
            livestreamRedisTemplate.delete(key);
    }

    public Map<String, LivestreamRedisData> getAllLivestreamData() {
        Set<String> keys = livestreamRedisTemplate.keys("vod:viewCount:*");
        Map<String, LivestreamRedisData> result = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                LivestreamRedisData data = safeGet(key);
                result.put(key.replace("vod:viewCount:", ""), data); // remove prefix for clarity
            }
        }

        return result;
    }
}
