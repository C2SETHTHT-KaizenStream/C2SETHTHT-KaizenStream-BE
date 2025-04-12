package com.example.KaizenStream_BE.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
    public void uploadM3u8ToMinIO(String streamId, String content) throws Exception {
        String objectName = "hls/" + streamId + "/playlist.m3u8";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, content.length(), -1)
                .contentType("application/x-mpegURL")
                .build());
    }
    public String getPresignedM3u8Url(String streamId, int expireInSeconds) throws Exception {
        String objectName = "hls/" + streamId + "/playlist.m3u8";

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expireInSeconds)
                .build());
    }

    public List<String> listTsFiles(String streamId) throws Exception {
        List<String> segmentFiles = new ArrayList<>();
        String prefix = "hls/" + streamId + "/";  // Đảm bảo sử dụng đúng prefix

        // Liệt kê tất cả các file trong thư mục đó
        Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)  // Đảm bảo tên bucket đúng
                .prefix(prefix)      // Đảm bảo prefix chính xác
                .build());

        // Lọc ra các file .ts
        for (Result<Item> result : objects) {
            Item item = result.get();
            String objectName = item.objectName();
            // System.out.println("Đã tìm thấy file: " + objectName);  // Log chi tiết các file
            if (objectName.endsWith(".ts")) {
                segmentFiles.add(objectName);
            }
        }

        if (segmentFiles.isEmpty()) {
            System.out.println("Không tìm thấy file .ts cho streamId: " + streamId);
        }
        segmentFiles=segmentFiles.stream().map(s -> s.substring(s.indexOf("/",s.indexOf("/")+1)+1,s.length()-3)).collect(Collectors.toList());

        segmentFiles = segmentFiles.stream()
                .sorted(Comparator.comparingInt(file -> Integer.parseInt(file.replaceAll("[^0-9]", "")))) // Loại bỏ ký tự không phải số
                .collect(Collectors.toList());


        segmentFiles.forEach(s -> System.out.println(s));
        return segmentFiles;
    }
//    public double getTsDurationFromMinio(String streamId, String ts) {
//        try {
//            // Thư mục tạm trong project
//            String tempDir = System.getProperty("user.dir") + File.separator + "temp_ts";
//            Files.createDirectories(Path.of(tempDir)); // Tạo thư mục nếu chưa có
//
//            // File tạm theo tên ts (ví dụ: 3.ts → temp_ts/3.ts)
//            Path tempFile = Path.of(tempDir, ts + ".ts");
//
//            // Tải file từ MinIO về thư mục tạm
//            minioClient.downloadObject(DownloadObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object("hls/" + streamId + "/" + ts + ".ts")
//                    .filename(tempFile.toString())
//                    .build());
//
//            // Đo thời lượng
//            double duration = getTsDurationUsingFFmpeg(tempFile.toString());
//
//            // Xoá file sau khi đo xong
//           // Files.deleteIfExists(tempFile);
//
//            return duration;
//        } catch (Exception e) {
//            System.err.println("❌ Lỗi đo thời lượng ts: " + e.getMessage());
//            return 2.0; // fallback
//        }
//    }
//
//
//    private double getTsDurationUsingFFmpeg(String filePath) {
//        log.warn("File line: "+filePath);
//        try {
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg", "-i", filePath,
//                    "-v", "error", "-show_entries", "format=duration",
//                    "-of", "default=noprint_wrappers=1:nokey=1"
//            );
//            Process process = pb.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = reader.readLine();
//            process.waitFor();
//            log.warn("line "+line);
//            return Double.parseDouble(line); // Trả về duration
//        } catch (Exception e) {
//            System.err.println("❌ Lỗi đo thời gian bằng FFmpeg: " + e.getMessage());
//            return 2.0; // fallback
//        }
//    }




}