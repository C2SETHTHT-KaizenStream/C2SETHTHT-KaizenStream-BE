//package com.example.KaizenStream_BE.service;
//
//import lombok.Data;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Service
//@Data
//@Getter
//public class StreamService {
//
//    // A map to store the last frame timestamp (in seconds) for each stream
//    public  final Map<String, Integer> streamEndTimes = new HashMap<>();
//    public  final Map<String, Integer> viewCount = new HashMap<>();
//
//    // Method to store the last frame timestamp for a given streamId
//    public void storeStreamEndTime(String streamId, int timeInSeconds) {
//        log.warn("storeStreamEndTime: "+ timeInSeconds);
//        streamEndTimes.put(streamId, timeInSeconds);
//    }
//
//    // Method to get the last frame timestamp for a given streamId
//    public int getStreamEndTime(String streamId) {
//        var duration= streamEndTimes.get(streamId).intValue();
//        streamEndTimes.remove(streamId);
//        return  duration;
//    }
//    // Method to store the last frame timestamp for a given streamId
//    public void storeViewCount(String streamId, Integer count) {
//        log.warn("storeViewCount: "+ count);
//        viewCount.put(streamId, count);
//    }
//
//    // Method to get the last frame timestamp for a given streamId
//    public int getViewCount(String streamId) {
//        var count= viewCount.get(streamId).intValue();
//        viewCount.remove(streamId);
//        return  count;
//    }
//
//
//
//}
