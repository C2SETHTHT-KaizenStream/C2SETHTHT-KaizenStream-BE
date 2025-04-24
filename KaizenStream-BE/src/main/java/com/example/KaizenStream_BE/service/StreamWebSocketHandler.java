//
//
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
//import java.util.concurrent.*;
//
//public class StreamWebSocketHandler extends BinaryWebSocketHandler {
//    private final Map<String, Process> ffmpegProcesses = new HashMap<>();
//    private final Map<String, OutputStream> ffmpegOutputs = new HashMap<>();
//    private final Map<String, BlockingQueue<byte[]>> dataQueues = new HashMap<>();
//    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);
//    private final Map<String, Integer> streamEndTimes = new HashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        session.setTextMessageSizeLimit(10024288);
//        session.setBinaryMessageSizeLimit(10024288);
//
//        // Lấy streamKey từ query params
//        String streamKey = "default_stream"; // Mặc định nếu không có streamKey
//        URI uri = session.getUri();
//        if (uri != null) {
//            Map<String, String> queryParams = splitQuery(uri.getQuery());
//            if (queryParams.containsKey("streamKey")) {
//                streamKey = queryParams.get("streamKey");
//            }
//        }
//
//        // Lưu streamKey vào WebSocketSession
//        session.getAttributes().put("streamKey", streamKey);
//
//        // Khởi động queue cho mỗi streamKey
//        dataQueues.put(streamKey, new LinkedBlockingQueue<>());
//
//        // Gửi ping mỗi 5 giây để giữ kết nối
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
//        // Khởi động FFmpeg process cho streamKey
//        startFFmpegProcess(streamKey);
//    }
//
//    // Khởi động FFmpeg cho mỗi streamKey
//    private void startFFmpegProcess(String streamKey) {
//        try {
//            // Gửi RTMP đến NGINX
//            ProcessBuilder rtmpPB = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", "pipe:0",
//                    "-c:v", "libx264",
//                    "-preset", "fast",
//                    "-b:v", "5000k",
//                    "-maxrate", "5000k",
//                    "-bufsize", "6000k",
//                    "-g", "60",
//                    "-c:a", "aac",
//                    "-b:a", "128k",
//                    "-ar", "44100",
//                    "-f", "flv",
//                    "rtmp://localhost:1936/live/" + streamKey
//            );
//            Process ffmpegProcess = rtmpPB.start();
//            OutputStream ffmpegOutput = ffmpegProcess.getOutputStream();
//
//            // Lưu tiến trình và output cho streamKey
//            ffmpegProcesses.put(streamKey, ffmpegProcess);
//            ffmpegOutputs.put(streamKey, ffmpegOutput);
//
//            // Log lỗi nếu có
//            startErrorLogger(ffmpegProcess, "FFmpeg RTMP",streamKey);
//
//            // Đọc dữ liệu từ queue và gửi vào FFmpeg
//            processQueueData(streamKey);
//
//        } catch (IOException e) {
//            System.err.println("❌ Lỗi khi khởi động FFmpeg cho streamKey: " + streamKey);
//        }
//    }
//    private void startErrorLogger(Process process, String name, String streamId) {
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.err.println(name + " LOG: " + line);
//                    if (line.contains("time=")) {
//                        String timeString = extractTimeFromLog(line);  // Extract time in HH:MM:SS.SS format
//                        if (timeString != null) {
//                            int timeInSeconds = convertTimeToSeconds(timeString);  // Convert to seconds
//
//                            if (streamId != null) {
//                                // Store the last frame timestamp (in seconds) for this stream
//                                streamEndTimes.put(streamId, timeInSeconds);
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                System.err.println("❌ Lỗi đọc log FFmpeg: " + e.getMessage());
//            }
//        }).start();
//    }
//    // Helper method to extract time (HH:MM:SS.SS) from the log line
//    private String extractTimeFromLog(String logLine) {
//        String timePrefix = "time=";
//        int startIndex = logLine.indexOf(timePrefix);
//        if (startIndex != -1) {
//            String timeString = logLine.substring(startIndex + timePrefix.length(), startIndex + timePrefix.length() + 11);  // Format: HH:MM:SS.SS
//            return timeString;
//        }
//        return null;
//    }
//
//    // Helper method to convert time (HH:MM:SS.SS) to seconds
//    private int convertTimeToSeconds(String timeString) {
//        String[] timeParts = timeString.split(":");
//        int hours = Integer.parseInt(timeParts[0]);
//        int minutes = Integer.parseInt(timeParts[1]);
//        double seconds = Double.parseDouble(timeParts[2]);
//
//        // Convert time to seconds (HH * 3600 + MM * 60 + SS)
//        return (int) (hours * 3600 + minutes * 60 + seconds);
//    }
//    // Đọc dữ liệu từ queue và gửi vào FFmpeg
//    private void processQueueData(String streamKey) {
//        new Thread(() -> {
//            try {
//                BlockingQueue<byte[]> queue = dataQueues.get(streamKey);
//                OutputStream output = ffmpegOutputs.get(streamKey);
//
//                while (true) {
//                    byte[] data = queue.take(); // Block cho đến khi có dữ liệu trong queue
//                    if (data.length > 0 && output != null) {
//                        output.write(data);
//                        output.flush();
//                    }
//                }
//            } catch (InterruptedException | IOException e) {
//                System.err.println("❌ Lỗi khi xử lý dữ liệu từ queue cho streamKey: " + streamKey);
//            }
//        }).start();
//    }
//
//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
//        String streamKey = (String) session.getAttributes().get("streamKey");
//        byte[] data = message.getPayload().array();
//        if (data.length == 0) {
//            return;
//        }
//
//        // Đưa dữ liệu vào queue
//        dataQueues.get(streamKey).offer(data); // Thêm dữ liệu vào queue
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String streamKey = (String) session.getAttributes().get("streamKey");
//        stopFFmpegProcess(streamKey);
//        dataQueues.remove(streamKey); // Xóa queue khi kết nối đóng
//    }
//
//    private void stopFFmpegProcess(String streamKey) throws InterruptedException {
//        Process process = ffmpegProcesses.get(streamKey);
//        OutputStream output = ffmpegOutputs.get(streamKey);
//        if (process != null && process.isAlive()) {
//            try {
//                if (output != null) output.close();
//                process.destroy();
//                ffmpegProcesses.remove(streamKey);
//                ffmpegOutputs.remove(streamKey);
//                System.out.println("✅ Dừng tiến trình FFmpeg cho streamKey: " + streamKey);
//            } catch (IOException e) {
//                System.err.println("❌ Lỗi khi dừng tiến trình FFmpeg cho streamKey: " + streamKey);
//            }
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
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        String payload = message.getPayload();
//        if ("ping".equals(payload)) {
//            System.out.println("🔄 Nhận ping từ client, giữ kết nối WebSocket...");
//            return;
//        }
//        System.err.println("⚠️ Nhận tin nhắn không mong muốn: " + payload);
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






package com.example.KaizenStream_BE.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreamWebSocketHandler extends BinaryWebSocketHandler {
    private final Map<String, Process> ffmpegProcesses = new HashMap<>();
    private final Map<String, OutputStream> ffmpegOutputs = new HashMap<>();
    private final Map<String, BlockingQueue<byte[]>> dataQueues = new HashMap<>();
    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Boolean> streamEnded = new HashMap<>();

    final LivestreamService livestreamService;
    final  LivestreamRedisService livestreamRedisService;

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

        // Khởi động queue cho mỗi streamKey
        dataQueues.put(streamKey, new LinkedBlockingQueue<>());

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
                    "-preset", "fast",
                    "-b:v", "5000k",
                    "-maxrate", "5000k",
                    "-bufsize", "6000k",
                    "-g", "60",
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
            startErrorLogger(ffmpegProcess, "FFmpeg RTMP", streamKey);

            // Đọc dữ liệu từ queue và gửi vào FFmpeg
            processQueueData(streamKey);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi khởi động FFmpeg cho streamKey: " + streamKey);
        }
    }
    private void startErrorLogger(Process process, String name,String streamId) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                   System.err.println(name + " LOGGGG: " + line);
                    if(streamEnded.containsKey(streamId)&&streamEnded.get(streamId)==true) {
                        if (line.contains("frame=") && line.contains("time=")) {
                            String frameStr = extractFrameFromLog(line);
                            System.out.println("frameStr: " + frameStr);

                            if (frameStr != null && !frameStr.trim().isEmpty()) {
                                int frame = Integer.parseInt(frameStr.trim());
                                if (frame > 0) {
                                    String timeString = extractTimeFromLog(line);
                                    System.out.println("timeInSeconds: " + timeString);

                                    if (timeString != null && !timeString.equals("N/A")) {
                                        try {
                                            int timeInSeconds = convertTimeToSeconds(timeString);
                                            System.out.println("timeInSeconds: " + timeInSeconds);
                                            log.warn("timeInSeconds: " + timeInSeconds);
                                            if (streamId != null) {
                                                log.warn("storeStreamEndTime: " + timeInSeconds);

                                                livestreamRedisService.saveOrUpdateDuration(streamId,timeInSeconds);
                                               // log.warn("storeStreamEndTime: " + streamService.getStreamEndTime(streamId));

                                            }
                                        } catch (NumberFormatException e) {
                                            System.err.println("⚠️ Lỗi parse timeString: " + timeString);
                                        }
                                    }
                                }
                            }
                        }
                        streamEnded.remove(streamId);
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Lỗi đọc log FFmpeg: " + e.getMessage());
            }
        }).start();
    }
    // Đọc dữ liệu từ queue và gửi vào FFmpeg
    private void processQueueData(String streamKey) {
        new Thread(() -> {
            try {
                BlockingQueue<byte[]> queue = dataQueues.get(streamKey);
                OutputStream output = ffmpegOutputs.get(streamKey);

                while (true) {
                    byte[] data = queue.take(); // Block cho đến khi có dữ liệu trong queue
                    if (data.length > 0 && output != null) {
                        output.write(data);
                        output.flush();
                    }
                }
            } catch (InterruptedException | IOException e) {
                System.err.println("❌ Lỗi khi xử lý dữ liệu từ queue cho streamKey: " + streamKey);
                System.err.println("❌ Lỗi : " + e.getMessage());

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

        // Đưa dữ liệu vào queue
        dataQueues.get(streamKey).offer(data); // Thêm dữ liệu vào queue
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String streamKey = (String) session.getAttributes().get("streamKey");
        System.out.println("afterConnectionClosed: " + streamKey);
        streamEnded.put(streamKey,true);
        Thread.sleep(1000);
        stopFFmpegProcess(streamKey);
        dataQueues.remove(streamKey); // Xóa queue khi kết nối đóng
    }

    private void stopFFmpegProcess(String streamKey) throws InterruptedException {
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
            System.out.println("🔄 Nhận ping từ client, giữ kết nối WebSocket..."+message) ;
            return;
        }
        System.err.println("⚠️ Nhận tin nhắn không mong muốn: " + payload);
    }



    private String extractTimeFromLog(String logLine) {
        System.out.println("🔄 extractTimeFromLog") ;

        String timePrefix = "time=";
        int startIndex = logLine.indexOf(timePrefix);
        if (startIndex != -1) {
            String timeString = logLine.substring(startIndex + timePrefix.length(), startIndex + timePrefix.length() + 11);  // Format: HH:MM:SS.SS
            return timeString;
        }
        return null;
    }

    // Helper method to convert time (HH:MM:SS.SS) to seconds
    private int convertTimeToSeconds(String timeString) {
        System.out.println("🔄 convertTimeToSeconds") ;

        String[] timeParts = timeString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        double seconds = Double.parseDouble(timeParts[2]);

        // Convert time to seconds (HH * 3600 + MM * 60 + SS)
        return (int) (hours * 3600 + minutes * 60 + seconds);
    }
    private String extractFrameFromLog(String logLine) {
        System.out.println("🔄 extractFrameFromLog " + logLine);
        Pattern pattern = Pattern.compile("frame=\\s*(\\d+)");
        Matcher matcher = pattern.matcher(logLine);

        if (matcher.find()) {
            String frame = matcher.group(1); // Lấy số sau "frame="
            System.out.println("✅ Extracted frame: " + frame);
            return frame;
        }

        return null;
    }


}




















