package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.LivestreamRedisData;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.enums.Status;
import com.example.KaizenStream_BE.mapper.LivestreamMapper;
import com.example.KaizenStream_BE.service.LivestreamRedisService;
import com.example.KaizenStream_BE.service.LivestreamService;
import com.example.KaizenStream_BE.service.MinioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RestController


@RequestMapping("/livestream")
public class LiveStreamController {
    @Value("${sync-hls-url}")
    String syncHlsUrl;
    @Autowired
    LivestreamService livestreamService;
    @Autowired
    LivestreamMapper livestreamMapper;
    // private final Map<String, Process> syncProcesses = new HashMap<>();
    private static Process syncProcess = null; // Chỉ có một tiến trình đồng bộ HLS
    @Autowired
//    private HistoryService historyService;


    private static  final AtomicInteger activeStreams = new AtomicInteger(0); // Đếm số luồng đang stream
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;


    @Autowired
    private MinioService minioService;




    @Autowired
    private LivestreamRedisService livestreamRedisService;
    @PostMapping
    ApiResponse<LivestreamRespone> createLivestream(@RequestBody @Valid CreateLivestreamRequest request){




        ApiResponse<LivestreamRespone> response= new ApiResponse<>();
        LivestreamRespone respone=livestreamService.createLivestream(request);
        return ApiResponse.<LivestreamRespone>builder().result(respone).build();
    }
    @GetMapping
    ApiResponse<List<LivestreamRespone>>  getAll(){
        return ApiResponse.<List<LivestreamRespone>>builder().result(livestreamService.getAll()).build();
    }
    @GetMapping("/paginated")
    public ApiResponse<Page<LivestreamRespone>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.<Page<LivestreamRespone>>builder()
                .result(livestreamService.getAllPaginated(page, size))
                .build();
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




    public  static  String processName="liveStream";




    @PostMapping("/start")
    public ApiResponse<String> startStream(@RequestParam String name) {
        name=getKey(name);


        System.out.println("🔴 Stream bắt đầu 1 live stream: "+name );


        int activeStreamCount = activeStreams.incrementAndGet();
        livestreamService.updateStatus(name, Status.ACTIVE);
        System.out.println("🔴 🔴 🔴  "+livestreamService.getLivestreamById(name).getStatus() );
        log.warn("activeStreamCount: "+activeStreamCount);
        if (syncProcess != null) {
            System.out.println("Sync process đang chạy");
        } else {
            System.out.println("Sync process chưa được khởi động");
        }





        return ApiResponse.<String>builder().result("Start new livestream").code(200).build();
    }


    @PostMapping("/end")
    public ResponseEntity<String> endStream(@RequestParam String name) throws InterruptedException {
        String streamKey=getKey(name);
        System.out.println("🛑 Dừng stream với streamKey: " + streamKey);


        int activeStreamCount = activeStreams.decrementAndGet();
        System.out.println("🛑 activeStreamCount " + activeStreamCount);

        System.out.println("⏳ Đợi 7 giây trước khi dừng tiến trình...");
        Thread.sleep(10000); // Chờ 10 giây (10,000 milliseconds)
        //stopSyncProcess();
        generateM3u8File(streamKey);
        // Nếu không còn luồng nào, dừng tiến trình đồng bộ



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
        streamId=getKey(streamId);


        return generateM3u8File(streamId);
    }


    @NotNull
    private ResponseEntity<String> generateM3u8File(String streamId) {
        try {
            List<String> tsFiles = minioService.listTsFiles(streamId);
            if (tsFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy file .ts");
            }


            String m3u8Content = generateM3u8Content(tsFiles,streamId);
            minioService.uploadM3u8ToMinIO(streamId, m3u8Content);
            log.warn("minioService");


            livestreamService.updateStatus(streamId, Status.ENDED);
            Thread.sleep(10000); // Chờ 10 giây (10,000 milliseconds)
            LivestreamRedisData data = livestreamRedisService.getData(streamId);
            Integer viewCount = data.getViewCount() != null ? data.getViewCount() : 0;
            Integer duration = data.getDuration() != null ? data.getDuration() : 0;
            log.warn("LivestreamRedisData ", viewCount, "\n",duration);


            livestreamService.updateLiveStream(streamId, viewCount, duration);


            return ResponseEntity.ok("Đã tạo và lưu playlist.m3u8 thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }


    private String generateM3u8Content(List<String> tsFileNames, String streamId) {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-TARGETDURATION:10\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:0\n");


        for (String ts : tsFileNames) {
            sb.append("#EXTINF:2.000,\n");
            String fileNameOnly = ts.substring(ts.lastIndexOf("/") + 1);
            sb.append( fileNameOnly+ ".ts\n");
        }






        sb.append("#EXT-X-ENDLIST\n");
        return sb.toString();
    }




    private  String getKey(String name){
        if(!name.contains(",")) return name;
        return name.substring(name.lastIndexOf(",")+1,name.length());
    }
    @GetMapping("/{streamId}/playlist-url")
    public ResponseEntity<String> getM3u8Url(@PathVariable String streamId) {
        try {
            String url = minioService.getPresignedM3u8Url(streamId, 3600); // 1 tiếng
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}

