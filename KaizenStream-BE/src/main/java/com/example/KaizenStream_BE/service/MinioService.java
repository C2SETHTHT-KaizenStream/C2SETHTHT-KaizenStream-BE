package com.example.KaizenStream_BE.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


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
//    public String getPresignedM3u8Url(String streamId, int expireInSeconds) throws Exception {
//        String objectName = "hls/" + streamId + "/playlist.m3u8";
//
//        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                .method(Method.GET)
//                .bucket(bucketName)
//                .object(objectName)
//                .expiry(expireInSeconds)
//                .build());
//    }

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





    public void downloadHlsToLocal(String streamId) throws Exception {
        String prefix = "hls/" + streamId + "/";
        String basePath = System.getProperty("user.dir");
        String localPath = basePath + "/temp/hls/" + streamId + "/";
        Files.createDirectories(Paths.get(localPath));

        Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build());

        for (Result<Item> result : items) {
            String objectName = result.get().objectName();
            if (objectName.endsWith(".ts")) {
                InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

                // Lấy tên file cuối cùng
                String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
                Path localFilePath = Paths.get(localPath, fileName);
                System.out.println("downloadHlsToLocal\n"+ fileName);
                System.out.println("localFilePath\n"+ localFilePath);

                Files.copy(stream, localFilePath, StandardCopyOption.REPLACE_EXISTING);
                stream.close();
            }
        }
    }

    public void uploadMultiQualityHLS(String streamId) throws Exception {
        String dir = "temp/hls/" + streamId + "/";
        File[] files = new File(dir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try (InputStream is = new FileInputStream(file)) {
                        minioClient.putObject(PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object("hls/" + streamId + "/" + file.getName())
                                .stream(is, file.length(), -1)
                                .contentType(file.getName().endsWith(".m3u8") ? "application/x-mpegURL" : "video/MP2T")
                                .build());
                    }
                }
            }
        }
    }

    public String getPresignedM3u8Url(String streamId, int expireSeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object("hls/" + streamId + "/master.m3u8")
                        .method(Method.GET)
                        .expiry(expireSeconds)
                        .build()
        );
    }

}
