package com.example.KaizenStream_BE.controller;

import com.cloudinary.Api;
import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.mapper.LivestreamMapper;
import com.example.KaizenStream_BE.service.LivestreamService;
import com.example.KaizenStream_BE.service.MinioService;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/livestream")
public class LiveStreamController {
    LivestreamService livestreamService;
    LivestreamMapper livestreamMapper;
   // private final Map<String, Process> syncProcesses = new HashMap<>();
    private static Process syncProcess = null; // Chỉ có một tiến trình đồng bộ HLS
    private static final AtomicInteger activeStreams = new AtomicInteger(0); // Đếm số luồng đang stream

    @Autowired
    private MinioService minioService;

    @PostMapping
    ApiResponse<LivestreamRespone> createLivestream(@RequestBody @Valid CreateLivestreamRequest request){
        ApiResponse<LivestreamRespone> response= new ApiResponse<>();
        response.setResult(livestreamMapper.toLivestreamRespone(livestreamService.createLivestream(request)));
        return response;
    }
    @GetMapping
    ApiResponse<List<LivestreamRespone>>  getAll(){
        return ApiResponse.<List<LivestreamRespone>>builder().result(livestreamService.getAll()).build();
    }
    @GetMapping("/{id}")
    ApiResponse<LivestreamRespone>  getLivestreamById(@PathVariable("id") String id){
         return ApiResponse.<LivestreamRespone>builder().result(livestreamService.getLivestreamById(id)).code(200).build();
    }
    @PutMapping()
    ApiResponse<LivestreamRespone> updateById(@RequestBody @Valid UpdateLivestreamRequest updateLivestreamRequest){
        return  ApiResponse.<LivestreamRespone>builder().result(livestreamService.updateLivestreamById(updateLivestreamRequest)).build();
    }
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteById(@PathVariable("id") String id){
        return ApiResponse.<String>builder().result(livestreamService.deleteById(id)).build();
    }





    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startStream(@RequestParam String name) {
        String streamKey = "stream-" + UUID.randomUUID().toString();
        Map<String, String> response = new HashMap<>();
        response.put("streamKey", streamKey);
        System.out.println("🔴 Stream bắt đầu với streamKey: " + streamKey);
        int activeStreamCount = activeStreams.incrementAndGet();
        if (activeStreamCount == 1 && syncProcess == null) {

        try {
            ProcessBuilder pb = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-File",
                    "D:/ApplicationSystem/nginx-rtmp/sync_hls.ps1", name);
            syncProcess = pb.start(); // Khởi tạo tiến trình đồng bộ
//            syncProcesses.put(name, process); // Lưu process để có thể dừng sau này


            System.out.println("✅ Script đồng bộ HLS đang chạy trong nền ");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi chạy PowerShell script: " + e.getMessage());
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Failed to start sync script"));
        }
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/end")
    public ResponseEntity<String> endStream(@RequestParam String name) {
        String streamKey=name;
        System.out.println("🛑 Dừng stream với streamKey: " + streamKey);
        int activeStreamCount = activeStreams.decrementAndGet();

        // Chờ 10 giây trước khi dừng tiến trình
        try {
            System.out.println("⏳ Đợi 7 giây trước khi dừng tiến trình...");
            Thread.sleep(7000); // Chờ 10 giây (10,000 milliseconds)
        } catch (InterruptedException e) {
            System.err.println("❌ Lỗi khi chờ trước khi dừng tiến trình: " + e.getMessage());
            return ResponseEntity.status(500).body("Error while waiting to stop stream");
        }
        // Nếu không còn luồng nào, dừng tiến trình đồng bộ
        if (activeStreamCount == 0 && syncProcess != null) {
            stopSyncProcess();
        }
        return ResponseEntity.ok("Stream ended");
    }
    private void stopSyncProcess() {
        if (syncProcess != null && syncProcess.isAlive()) {
            syncProcess.destroy();  // Dừng tiến trình đồng bộ
            syncProcess = null;  // Đặt lại tiến trình đồng bộ để có thể chạy lại sau
            System.out.println("✅ Dừng tiến trình đồng bộ HLS.");
        }
    }


    @PostMapping("/{streamId}/generate-m3u8")
    public ResponseEntity<String> generateM3u8(@PathVariable String streamId) {
        try {
            List<String> tsFiles = minioService.listTsFiles(streamId);
            if (tsFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy file .ts");
            }

            String m3u8Content = generateM3u8Content(tsFiles);
            minioService.uploadM3u8ToMinIO(streamId, m3u8Content);

            return ResponseEntity.ok("Đã tạo và lưu playlist.m3u8 thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
    private String generateM3u8Content(List<String> tsFileNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-TARGETDURATION:10\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:0\n");

        for (String ts : tsFileNames) {
            sb.append("#EXTINF:10.0,\n");
            sb.append(ts + ".ts\n");
        }

        sb.append("#EXT-X-ENDLIST\n");
        return sb.toString();
    }

}
