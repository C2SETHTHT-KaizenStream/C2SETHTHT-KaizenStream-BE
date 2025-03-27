//package com.example.KaizenStream_BE.service;
//
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//
//import java.io.*;
//import java.net.URI;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class StreamWebSocketHandler extends BinaryWebSocketHandler {
//    private Process ffmpegRTMPProcess;
//
//    private OutputStream ffmpegRTMPInput;
//
//    private String streamKey = "default_stream";
//    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);
//
//
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        session.setTextMessageSizeLimit(10024288);
//        session.setBinaryMessageSizeLimit(10024288);
//
//        // 🔹 Lấy streamKey từ query params
//        URI uri = session.getUri();
//        if (uri != null) {
//            Map<String, String> queryParams = splitQuery(uri.getQuery());
//            if (queryParams.containsKey("streamKey")) {
//                streamKey = queryParams.get("streamKey");
//            }
//        }
//        // 🔹 Gửi ping mỗi 5 giây để giữ kết nối
//        pingScheduler.scheduleAtFixedRate(() -> {
//            try {
//                if (session.isOpen()) {
//                    session.sendMessage(new TextMessage("ping"));
//                }
//            } catch (IOException e) {
//                System.err.println("Lỗi gửi ping: " + e.getMessage());
//            }
//        }, 5, 5, TimeUnit.SECONDS);
//
//
//        startFFmpegProcesses();
//    }
//
//
//
//
//
//    private void startFFmpegProcesses() {
//        try {
//            // 🔹 Gửi RTMP đến NGINX
//            ProcessBuilder rtmpPB = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", "pipe:0",
//                    "-c:v", "libx264",
//                    "-preset", "veryfast",
//                    "-b:v", "3000k",
//                    "-maxrate", "3000k",
//                    "-bufsize", "6000k",
//                    "-g", "50",
//                    "-c:a", "aac",
//                    "-b:a", "128k",
//                    "-ar", "44100",
//                    "-f", "flv",
//                    "rtmp://localhost:1936/live/" + streamKey,
//                    "-loglevel", "error" // chỉ ghi log lỗi
//
//            );
//            ffmpegRTMPProcess = rtmpPB.start();
//            ffmpegRTMPInput = ffmpegRTMPProcess.getOutputStream();
//
//
//            // 🔹 Log lỗi nếu có
//            startErrorLogger(ffmpegRTMPProcess, "FFmpeg RTMP");
//
//
//        } catch (IOException e) {
//            System.err.println("❌ Lỗi khi khởi động FFmpeg: " + e.getMessage());
//        }
//    }
//
//    private void startErrorLogger(Process process, String name) {
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.err.println(name + " LOG: " + line);
//                }
//            } catch (IOException e) {
//                System.err.println("❌ Lỗi đọc log FFmpeg: " + e.getMessage());
//            }
//        }).start();
//    }
//
//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
//        byte[] data = message.getPayload().array();
//        if (data.length == 0) {
//            return;
//        }
//
//        // 🔹 Gửi dữ liệu vào FFmpeg RTMP
//        writeToFFmpeg(ffmpegRTMPProcess, ffmpegRTMPInput, data, "RTMP");
//
//        // 🔹 Gửi dữ liệu vào FFmpeg Video RTP
//
//    }
//
//    private void writeToFFmpeg(Process process, OutputStream input, byte[] data, String name) {
//        if (process != null && process.isAlive() && input != null) {
//            try {
//                input.write(data);
//                input.flush();
//            } catch (IOException e) {
//                System.err.println("❌ Lỗi khi ghi dữ liệu vào FFmpeg " + name + ": " + e.getMessage());
//            }
//        } else {
//            System.err.println("⚠️ FFmpeg " + name + " đã dừng hoặc pipe đã đóng, không thể ghi dữ liệu!");
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        System.out.println("🛑 Client đã ngắt kết nối.");
//        stopFFmpegProcess(ffmpegRTMPProcess, ffmpegRTMPInput);
//
//    }
//
//    private void stopFFmpegProcess(Process process, OutputStream input) {
//        try {
//            if (input != null) input.close();
//            if (process != null) process.destroy();
//        } catch (IOException e) {
//            System.err.println("❌ Lỗi khi đóng FFmpeg: " + e.getMessage());
//        }
//    }
//
//    private static Map<String, String> splitQuery(String query) {
//        Map<String, String> queryPairs = new HashMap<>();
//        if (query == null || query.isEmpty()) {
//            return queryPairs;
//        }
//
//        String[] pairs = query.split("&");
//        for (String pair : pairs) {
//            int idx = pair.indexOf("=");
//            if (idx > 0) {
//                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
//                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
//                queryPairs.put(key, value);
//            }
//        }
//        return queryPairs;
//    }
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        String payload = message.getPayload();
//        if ("ping".equals(payload)) {
//            System.out.println("🔄 Nhận ping từ client, giữ kết nối WebSocket...");
//            return; // Không xử lý gì thêm
//        }
//        System.err.println("⚠️ Nhận tin nhắn không mong muốn: " + payload);
//    }
//
//}



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
    // Lưu trữ các tiến trình FFmpeg cho từng streamKey
    private final Map<String, Process> ffmpegProcesses = new HashMap<>();
    private final Map<String, OutputStream> ffmpegOutputs = new HashMap<>();
    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(10024288);
        session.setBinaryMessageSizeLimit(10024288);

        // 🔹 Lấy streamKey từ query params
        URI uri = session.getUri();
        String streamKey = "default_stream"; // Mặc định nếu không có streamKey
        if (uri != null) {
            Map<String, String> queryParams = splitQuery(uri.getQuery());
            if (queryParams.containsKey("streamKey")) {
                streamKey = queryParams.get("streamKey");
            }
        }

        // 🔹 Gửi ping mỗi 5 giây để giữ kết nối
        pingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("ping"));
                }
            } catch (IOException e) {
                System.err.println("Lỗi gửi ping: " + e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);

        // Khởi động tiến trình FFmpeg cho luồng stream này
        startFFmpegProcess(streamKey);
    }

    // Khởi động tiến trình FFmpeg cho mỗi streamKey
    private void startFFmpegProcess(String streamKey) {
        try {
            // Gửi RTMP đến NGINX
            ProcessBuilder rtmpPB = new ProcessBuilder(
                    "ffmpeg",
                    "-i", "pipe:0",
                    "-c:v", "libx264",
                    "-preset", "veryfast",
                    "-b:v", "3000k",
                    "-maxrate", "3000k",
                    "-bufsize", "6000k",
                    "-g", "50",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-ar", "44100",
                    "-f", "flv",
                    "rtmp://localhost:1936/live/" + streamKey,
                    "-loglevel", "error" // chỉ ghi log lỗi
            );
            Process process = rtmpPB.start();
            OutputStream output = process.getOutputStream();
            ffmpegProcesses.put(streamKey, process);
            ffmpegOutputs.put(streamKey, output);

            System.out.println("✅ FFmpeg process đã khởi động cho streamKey: " + streamKey);
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi khởi động FFmpeg cho streamKey: " + streamKey);
        }
    }

    // Dừng tiến trình FFmpeg khi kết nối WebSocket đóng
    private void stopFFmpegProcess(String streamKey) {
        Process process = ffmpegProcesses.get(streamKey);
        OutputStream output = ffmpegOutputs.get(streamKey);
        if (process != null && process.isAlive()) {
            try {
                if (output != null) {
                    output.close();
                }
                process.destroy();
                ffmpegProcesses.remove(streamKey);
                ffmpegOutputs.remove(streamKey);
                System.out.println("✅ Dừng tiến trình FFmpeg cho streamKey: " + streamKey);
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi dừng tiến trình FFmpeg cho streamKey: " + streamKey);
            }
        }
    }

    // Xử lý tin nhắn binary (video stream)
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String streamKey = getStreamKeyFromSession(session); // Lấy streamKey từ session
        byte[] data = message.getPayload().array();
        if (data.length == 0) {
            return;
        }

        // Gửi dữ liệu vào FFmpeg RTMP
        writeToFFmpeg(streamKey, data);
    }

    // Ghi dữ liệu vào tiến trình FFmpeg
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
            System.err.println("⚠️ FFmpeg process không tồn tại hoặc đã dừng cho streamKey: " + streamKey);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("🛑 Client đã ngắt kết nối.");
        String streamKey = getStreamKeyFromSession(session);
        stopFFmpegProcess(streamKey);
    }

    private String getStreamKeyFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            Map<String, String> queryParams = splitQuery(uri.getQuery());
            return queryParams.get("streamKey");
        }
        return "default_stream"; // Return default streamKey if not found
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
