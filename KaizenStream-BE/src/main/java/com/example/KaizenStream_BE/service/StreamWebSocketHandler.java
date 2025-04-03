

package com.example.KaizenStream_BE.service;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamWebSocketHandler extends BinaryWebSocketHandler {
    private final Map<String, Process> ffmpegProcesses = new HashMap<>();
    private final Map<String, OutputStream> ffmpegOutputs = new HashMap<>();
    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(10024288);
        session.setBinaryMessageSizeLimit(10024288);

        // Lấy streamKey từ query params
        String streamKey = "default_stream"; // Mặc định nếu không có streamKey
        URI uri = session.getUri();
        if (uri != null) {
            Map<String, String> queryParams = splitQuery(uri.getQuery());
            if (queryParams.containsKey("streamKey")) {
                streamKey = queryParams.get("streamKey");
            }
        }

        // Lưu streamKey vào WebSocketSession
        session.getAttributes().put("streamKey", streamKey);

        // Gửi ping mỗi 5 giây để giữ kết nối
        pingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("ping"));
                }
            } catch (IOException e) {
                System.err.println("Lỗi gửi ping: " + e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);

        // Khởi động FFmpeg process cho streamKey
        startFFmpegProcess(streamKey);
    }

    // Khởi động FFmpeg cho mỗi streamKey
    private void startFFmpegProcess(String streamKey) {
        try {
            // Gửi RTMP đến NGINX
            ProcessBuilder rtmpPB = new ProcessBuilder(
                    "ffmpeg",
                    "-i", "pipe:0",
                    "-c:v", "libx264",
                    "-preset", "veryfast",
                    "-b:v", "5000k",
                    "-maxrate", "5000k",
                    "-bufsize", "6000k",
                    "-g", "70",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-ar", "44100",
                    "-f", "flv",
                    "rtmp://localhost:1936/live/" + streamKey
            );
            Process ffmpegProcess = rtmpPB.start();
            OutputStream ffmpegOutput = ffmpegProcess.getOutputStream();

            // Lưu tiến trình và output cho streamKey
            ffmpegProcesses.put(streamKey, ffmpegProcess);
            ffmpegOutputs.put(streamKey, ffmpegOutput);

            // Log lỗi nếu có
            startErrorLogger(ffmpegProcess, "FFmpeg RTMP");

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi khởi động FFmpeg cho streamKey: " + streamKey);
        }
    }

    private void startErrorLogger(Process process, String name) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(name + " LOG: " + line);
                }
            } catch (IOException e) {
                System.err.println("❌ Lỗi đọc log FFmpeg: " + e.getMessage());
            }
        }).start();
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String streamKey = (String) session.getAttributes().get("streamKey");
        byte[] data = message.getPayload().array();
        if (data.length == 0) {
            return;
        }

        // Gửi dữ liệu vào FFmpeg RTMP
        writeToFFmpeg(streamKey, data);
    }

    private void writeToFFmpeg(String streamKey, byte[] data) {
        Process process = ffmpegProcesses.get(streamKey);
        OutputStream output = ffmpegOutputs.get(streamKey);
        if (process != null && process.isAlive() && output != null) {
            try {
                output.write(data);
                output.flush();
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi ghi dữ liệu vào FFmpeg cho streamKey: " + streamKey);
            }
        } else {
            System.err.println("⚠️ FFmpeg process đã dừng hoặc pipe đã đóng cho streamKey: " + streamKey);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String streamKey = (String) session.getAttributes().get("streamKey");
        stopFFmpegProcess(streamKey);
    }

    private void stopFFmpegProcess(String streamKey) {
        Process process = ffmpegProcesses.get(streamKey);
        OutputStream output = ffmpegOutputs.get(streamKey);
        if (process != null && process.isAlive()) {
            try {
                if (output != null) output.close();
                process.destroy();
                ffmpegProcesses.remove(streamKey);
                ffmpegOutputs.remove(streamKey);
                System.out.println("✅ Dừng tiến trình FFmpeg cho streamKey: " + streamKey);
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi dừng tiến trình FFmpeg cho streamKey: " + streamKey);
            }
        }
    }

    private static Map<String, String> splitQuery(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryPairs;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                queryPairs.put(key, value);
            }
        }
        return queryPairs;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            System.out.println("🔄 Nhận ping từ client, giữ kết nối WebSocket...");
            return;
        }
        System.err.println("⚠️ Nhận tin nhắn không mong muốn: " + payload);
    }
}




















