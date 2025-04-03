//package com.example.KaizenStream_BE.configuration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config){
//        config.enableSimpleBroker("/topic"); //serve gửi tin nhắn tới client
//        config.setApplicationDestinationPrefixes("/app");// client gửi message đến server
//        //topic /cricket
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry){
//        registry.addEndpoint("/ws").setAllowedOrigins(feUrl,"http://localhost:63342").withSockJS(); //  mở kết nối
//    }
//}


package com.example.KaizenStream_BE.configuration;

import com.example.KaizenStream_BE.service.StreamWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
    @Value("${fe-url}")
    protected String feUrl;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Serve gửi tin nhắn tới client
        config.setApplicationDestinationPrefixes("/app"); // Client gửi message đến server
        config.setUserDestinationPrefix("/user"); // Định nghĩa tiền tố cho tin nhắn đến từng user
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (accessor.getCommand() != null && accessor.getCommand().equals(StompCommand.CONNECT)) {
                    String userId = accessor.getFirstNativeHeader("userId");
                    if (userId != null) {
                        log.info("✅ WebSocket connected with userId: {}", userId);
                        accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, null));
                    }
                }
                return message;
            }
        });
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
//        registry.addEndpoint("/ws").setAllowedOrigins(feUrl).withSockJS(); //  mở kết nối
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(feUrl).withSockJS(); //  mở kết nối

    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new StreamWebSocketHandler(), "/stream")
                .setAllowedOrigins(feUrl) // Chỉ định URL frontend
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        response.getHeaders().set("Connection", "keep-alive");
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                        // Có thể log lại nếu cần
                    }
                });
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    // Cấu hình CORS chuẩn để hỗ trợ WebSocket


    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); // Để chạy ping giữ kết nối

    }
}
