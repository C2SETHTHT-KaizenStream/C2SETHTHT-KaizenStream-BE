package com.example.KaizenStream_BE.configuration;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * Lớp này cấu hình Redis làm bộ nhớ cache cho ứng dụng Spring Boot.
     * Dữ liệu trong cache được lưu dưới dạng key-value, với key là chuỗi (String) và value là JSON.
     * Cache tự động hết hạn sau 10 phút, giúp tiết kiệm bộ nhớ và đảm bảo dữ liệu không bị cũ quá lâu.
     * Hỗ trợ các kiểu dữ liệu Java 8 như LocalDateTime nhờ JavaTimeModule.
     **/

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Tạo ObjectMapper an toàn, không dùng DefaultTyping
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Hỗ trợ kiểu dữ liệu Java 8 như LocalDateTime
        objectMapper.findAndRegisterModules(); // Đăng ký các module cần thiết

        // Không sử dụng DefaultTyping để tránh lưu metadata thừa
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Cache hết hạn sau 10 phút
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * @param redisConnectionFactory
     * @return Cấu hình RedisTemplate để lưu dữ liệu dạng JSON vào Redis.
     * ObjectMapper hỗ trợ kiểu dữ liệu Java 8 như LocalDateTime nhờ JavaTimeModule.
     */
    @Bean
    public RedisTemplate<String, ChatResponse> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ChatResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Cấu hình serializer cho key và value
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();


        return template;
    }

}