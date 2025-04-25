package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.chart.MonthlyViewerCountDTO;
import com.example.KaizenStream_BE.dto.respone.chart.TopUserDTO;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.enums.Status;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.LivestreamMapper;
import com.example.KaizenStream_BE.repository.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LivestreamService {
    LivestreamRepository livestreamRepository;
    LivestreamMapper livestreamMapper;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    ScheduleRepository scheduleRepository;
    TagRepository tagRepository;
    ProfileRepository profileRepository;
    UserPreferencesRepository   userPreferencesRepository;


    public LivestreamRespone createLivestream(CreateLivestreamRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        List<Category> categoryEntities = request.getCategories().stream()
                .map(name -> {
                    return categoryRepository.findFirstByNameIgnoreCase(name)
                            .orElseGet(() -> {
                                Category category = new Category();
                                category.setName(name);
                                categoryRepository.save(category);
                                return category;
                            });
                })
                .collect(Collectors.toList());
        List<Tag> tags = request.getTags().stream().map(name -> {
            return tagRepository.findFirstByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Tag tag = new Tag();
                        tag.setName(name);
                        tagRepository.save(tag);
                        return tag;
                    });
        }).collect(Collectors.toList());
        Livestream livestream = livestreamMapper.toLivestream(request);
        livestream.setCategories(categoryEntities);
        livestream.setTags(tags);
        livestream.setUser(user);

        if (request.getScheduleId() != null && !request.getScheduleId().isEmpty()) {
            Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXIST));
            livestream.setSchedule(schedule);
            if (schedule.getScheduleTime().after(new Date())) {
                // Thiết lập thông tin lịch trình cho livestream
                livestream.setStartTime(schedule.getScheduleTime());

                livestream.setStatus(Status.PENDING.getDescription());  // Trạng thái chờ khi có lịch trình
                // Cập nhật status của schedule (Nếu cần)
                schedule.setStatus(Status.PENDING.getDescription());
            } else {
                schedule.setStatus(Status.ENDED.getDescription());
                livestream.setStatus(Status.INACTIVE.getDescription());
            }
            scheduleRepository.save(schedule);
        } else {
            // Nếu không có lịch trình, livestream bắt đầu ngay lập tức
            livestream.setStartTime(new Date());
            livestream.setStatus(Status.INACTIVE.getDescription());
        }

        livestreamRepository.save(livestream);
        LivestreamRespone livestreamRespone = livestreamMapper.toLivestreamRespone(livestream);
        List<String> categoryList = livestream.getCategories().stream().map(c -> c.getName()).toList();
        List<String> tagList = livestream.getTags().stream().map(c -> c.getName()).toList();
        livestreamRespone.setCategories(categoryList);
        livestreamRespone.setTags(tagList);
        return livestreamRespone;
    }

    public LivestreamRespone getLivestreamById(String id) {
        return livestreamMapper.toLivestreamRespone(livestreamRepository.findById(id).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS")));
    }

    public List<LivestreamRespone> getAll() {
        var live = livestreamRepository.findAll();

        return live.stream().map(livestream -> {
            LivestreamRespone livestreamRespone = livestreamMapper.toLivestreamRespone(livestream);
            List<String> categoryList = livestream.getCategories().stream().map(c -> c.getName()).toList();
            List<String> tagList = livestream.getTags().stream().map(c -> c.getName()).toList();
            livestreamRespone.setCategories(categoryList);
            livestreamRespone.setTags(tagList);
            User streamer = livestream.getUser();
            log.warn("streamer: " + streamer.getUserId());
//            Profile profile=profileRepository.findByUser_UserId(streamer.getUserId()).orElseThrow(()-> new RuntimeException("PROFILE_NOT_EXITS"));
//
//            livestreamRespone.setStreamerImgUrl(profile.getAvatarUrl());
            livestreamRespone.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");
            log.warn("setStreamerImgUrl: " + livestreamRespone.getStreamerImgUrl());

            livestreamRespone.setStreamerId(streamer.getUserId());
            log.warn("streamer: " + livestreamRespone.getStreamerId());


            return livestreamRespone;

        }).toList();
    }


    public LivestreamRespone updateLivestreamById(@Valid UpdateLivestreamRequest updateLivestreamRequest) {
        var live = livestreamRepository.findById(updateLivestreamRequest.getLivestreamId()).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        livestreamMapper.updateLivestream(live, updateLivestreamRequest);


        List<Category> categoryEntities = updateLivestreamRequest.getCategories().stream()
                .map(name -> {
                    return categoryRepository.findFirstByNameIgnoreCase(name)
                            .orElseGet(() -> {
                                Category category = new Category();
                                category.setName(name);
                                categoryRepository.save(category);
                                return category;
                            });
                })
                .collect(Collectors.toList());
        List<Tag> tags = updateLivestreamRequest.getTags().stream().map(name -> {
            return tagRepository.findFirstByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Tag tag = new Tag();
                        tag.setName(name);
                        tagRepository.save(tag);
                        return tag;
                    });
        }).collect(Collectors.toList());
        live.setCategories(categoryEntities);
        live.setTags(tags);
        livestreamRepository.save(live);

        List<String> categoryList = live.getCategories().stream().map(c -> c.getName()).toList();
        List<String> tagList = live.getTags().stream().map(c -> c.getName()).toList();
        LivestreamRespone livestreamRespone = livestreamMapper.toLivestreamRespone(live);
        livestreamRespone.setTags(tagList);
        livestreamRespone.setCategories(categoryList);
        return livestreamRespone;
    }


    public String deleteById(String id) {
        var live = livestreamRepository.findById(id).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));

        livestreamRepository.deleteById(id);
        return "Livestream has been deleted";
    }

    public void updateStatus(String streamId, Status status) {
        var live = livestreamRepository.findById(streamId).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setStatus(status.getDescription());
        log.warn("updateStatus: " + live.getStatus());
        System.out.println("updateStatus:updateStatus:" + live.getStatus());

        livestreamRepository.save(live);
    }

    public void updateLiveStreamDuration(String streamId, int duration) {
        var live = livestreamRepository.findById(streamId).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        System.out.println("duration :" + duration);

        live.setDuration(duration);
        livestreamRepository.save(live);
        System.out.println("duration :" + live.getDuration());
        livestreamRepository.flush(); // ép Hibernate ghi xuống DB ngay


    }

    public void updateLiveStreamViewCount(String streamId, int viewCount) {
        var live = livestreamRepository.findById(streamId).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setViewerCount(viewCount);

        livestreamRepository.save(live);
    }

    public void updateLiveStream(String streamId, int viewCount, int duration) {
        var live = livestreamRepository.findById(streamId).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setViewerCount(viewCount);
        live.setDuration(duration);
        System.out.println("viewcount:" + live.getViewerCount());
        System.out.println("duration :" + live.getDuration());
        livestreamRepository.save(live);
    }


    //    public Page<LivestreamRespone> getAllPaginated(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
//        Page<Livestream> livePage = livestreamRepository.findAll(pageable);
//
//        return livePage.map(livestream -> {
//            System.out.println("Viewer count: "+livestream.getViewerCount());
//            LivestreamRespone response = livestreamMapper.toLivestreamRespone(livestream);
//            response.setCategories(livestream.getCategories().stream().map(c -> c.getName()).toList());
//            response.setTags(livestream.getTags().stream().map(t -> t.getName()).toList());
//            User streamer=livestream.getUser();
//            log.warn("streamer: "+streamer.getUserId());
////            Profile profile=profileRepository.findByUser_UserId(streamer.getUserId()).orElseThrow(()-> new RuntimeException("PROFILE_NOT_EXITS"));
////
////            livestreamRespone.setStreamerImgUrl(profile.getAvatarUrl());
//            response.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");
//            log.warn("setStreamerImgUrl: "+response.getStreamerImgUrl());
//
//            response.setStreamerId(streamer.getUserId());
//            log.warn("streamer: "+response.getStreamerId());
//            response.setChannelName("DTHHH");
//            return response;
//        });
//    }
//
//    public Page<LivestreamRespone> getLiveStreamOrderByViewCountPaginate(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Livestream> livePage = livestreamRepository.findByStatusOrderByViewerCountDesc(
//                Status.ACTIVE.getDescription(), pageable);
//
//        return livePage.map(livestream -> {
//            System.out.println("Viewer count: "+livestream.getViewerCount());
//            LivestreamRespone response = livestreamMapper.toLivestreamRespone(livestream);
//            response.setCategories(livestream.getCategories().stream().map(c -> c.getName()).toList());
//            response.setTags(livestream.getTags().stream().map(t -> t.getName()).toList());
//            User streamer=livestream.getUser();
//            log.warn("streamer: "+streamer.getUserId());
////            Profile profile=profileRepository.findByUser_UserId(streamer.getUserId()).orElseThrow(()-> new RuntimeException("PROFILE_NOT_EXITS"));
////
////            livestreamRespone.setStreamerImgUrl(profile.getAvatarUrl());
//            response.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");
//            log.warn("setStreamerImgUrl: "+response.getStreamerImgUrl());
//
//            response.setStreamerId(streamer.getUserId());
//            log.warn("streamer: "+response.getStreamerId());
//            response.setChannelName("DTHHH");
//            return response;
//        });
//    }
    public Page<LivestreamRespone> getAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        return livestreamRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<LivestreamRespone> getRecommendedLivestreams(String userId, int page, int size) {
        // 1. Lấy preferences của user
        UserPreferences prefs = userPreferencesRepository
                .findByUser_UserId(userId)
                .orElse(null);

        // 2. Lấy ngẫu nhiên tối đa 5 tag, 5 category
        List<String> tagsList;
        List<String> catsList;

        if (!Objects.isNull(prefs)) {
            tagsList = new ArrayList<>(prefs.getPreferredTags());
            catsList = new ArrayList<>(prefs.getPreferredCategories());
            Collections.shuffle(tagsList);
            Collections.shuffle(catsList);
        } else {
            return getAllPaginated(page, size);
        }

        List<String> pickTags = tagsList.stream().limit(5).toList();
        List<String> pickCats = catsList.stream().limit(5).toList();

        // 3. Tạo pageable với sort viewerCount desc
        Pageable pageable = PageRequest.of(page, size);

        // 4. Gọi repository tùy theo điều kiện lọc
        Page<Livestream> pageLives = livestreamRepository
                .findActiveByTagsOrCategoriesOrderByViewerCountDesc(
                        Status.ACTIVE.getDescription(),
                        pickTags,
                        pickCats,
                        pageable
                );
        // 5. Map về response
        return pageLives.map(this::mapToResponse);
    }

    private LivestreamRespone mapToResponse(Livestream livestream) {
        // 1. chuyển đổi cơ bản
        LivestreamRespone resp = livestreamMapper.toLivestreamRespone(livestream);
        // 2. categories & tags
        resp.setCategories(
                livestream.getCategories()
                        .stream()
                        .map(Category::getName)
                        .toList()
        );
        resp.setTags(
                livestream.getTags()
                        .stream()
                        .map(Tag::getName)
                        .toList()
        );
        // 3. streamer info
        User streamer = livestream.getUser();
        resp.setStreamerId(streamer.getUserId());
        resp.setStreamerUsername(streamer.getUserName());
        // nếu có ProfileRepository, bạn có thể fetch avatar thật
//         String avatar = profileRepository
//             .findByUser_UserId(streamer.getUserId())
//             .orElseThrow(() -> new RuntimeException("PROFILE_NOT_EXISTS"))
//             .getAvatarUrl();
//         resp.setStreamerImgUrl(avatar);
        resp.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");
        // 4. channel name (hoặc fetch động nếu cần)
        resp.setChannelName("DTHHH");
        return resp;
    }

    public LivestreamRespone getLastLiveStream(String streamerId) {
        Livestream live = livestreamRepository.findTopInactiveLivestreamByStreamerOrderedByStartTimeDesc(streamerId)
                .orElse(null);
        return mapToResponse(live);
    }

    public LivestreamRespone getLastLiveStreamOnStreamerBaseOnLive(String livestreamId) {
        Livestream live = livestreamRepository.findById(livestreamId)
                .orElseThrow(() -> new RuntimeException("Error: Cannot find livestream"));
        return getLastLiveStream(live.getUser().getUserId());
    }


    public void stopLive(String livestreamId, int viewCount) {
        log.warn("stopLivestopLivestopLivestopLivestopLive: " + viewCount);
        var live = livestreamRepository.findById(livestreamId).orElseThrow(() -> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setViewerCount(viewCount);
        livestreamRepository.save(live);
        log.warn("setViewerCountsetViewerCount: " + live.getViewerCount());
        livestreamRepository.flush(); // ép Hibernate ghi xuống DB ngay


    }

    public Page<LivestreamRespone> getLivestreamsByUserId(String userId, Pageable pageable) {
        Page<Livestream> livestreamPage = livestreamRepository.findByUser_UserId(userId, pageable);

        return livestreamPage.map(livestream -> {
            LivestreamRespone response = livestreamMapper.toLivestreamRespone(livestream);

            response.setCategories(livestream.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toList()));

            response.setTags(livestream.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));

            User streamer = livestream.getUser();
            response.setStreamerId(streamer.getUserId());

            response.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");

            return response;
        });
    }

    public Page<LivestreamRespone> searchLivestreams(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Livestream> livestreamPage = livestreamRepository.findByTitleContainingIgnoreCase(title, pageable);
        if (livestreamPage.isEmpty()) {
            livestreamPage = livestreamRepository.findByDescriptionContainingIgnoreCase(title, pageable);
        }


        return livestreamPage.map(livestream -> {
            LivestreamRespone response = livestreamMapper.toLivestreamRespone(livestream);

            response.setCategories(livestream.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toList()));

            response.setTags(livestream.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));

            User streamer = livestream.getUser();
            response.setStreamerId(streamer.getUserId());

            response.setStreamerImgUrl("http://res.cloudinary.com/dpu7db88i/image/upload/v1744616662/zsjs39hx6rdtojm4i9jt.webp");

            return response;
        });
    }


    // Phương thức lấy tổng lượt xem theo tháng
    public ApiResponse<List<MonthlyViewerCountDTO>> getMonthlyViewerCounts() {
        try {
            // Lấy kết quả từ Repository
            List<Object[]> results = livestreamRepository.getMonthlyViewerCounts();

            // Nếu không có dữ liệu
            if (results.isEmpty()) {
                return ApiResponse.<List<MonthlyViewerCountDTO>>builder()
                        .code(1001)
                        .message("No data found")
                        .result(null)
                        .status("ERROR")
                        .build();  // Trả về lỗi 1001 nếu không có kết quả
            }

            // Chuyển đổi kết quả thành DTO
            List<MonthlyViewerCountDTO> viewerCounts = new ArrayList<>();
            for (Object[] result : results) {
                String month = (String) result[0];  // Tháng (Jan, Feb, Mar, ...)
                int views = (int) result[1];        // Tổng số lượt xem trong tháng
                viewerCounts.add(new MonthlyViewerCountDTO(month, views));
            }

            // Trả về dữ liệu thành công
            return ApiResponse.<List<MonthlyViewerCountDTO>>builder()
                    .code(1000)
                    .message("Monthly viewer counts fetched successfully")
                    .result(viewerCounts)
                    .status("SUCCESS")
                    .build();  // Trả về DTO

        } catch (Exception e) {
            // Trả về lỗi nếu gặp ngoại lệ
            return ApiResponse.<List<MonthlyViewerCountDTO>>builder()
                    .code(1002)
                    .message("An error occurred: " + e.getMessage())
                    .result(null)
                    .status("ERROR")
                    .build();  // Trả về lỗi khi có ngoại lệ
        }
    }

    public ApiResponse<List<TopUserDTO>> getTopUsersByViewCount() {
        try {
            // Lấy kết quả từ Repository
            List<Object[]> results = livestreamRepository.getTopUsersByViewCount();

            // Nếu không có dữ liệu
            if (results.isEmpty()) {
                return ApiResponse.<List<TopUserDTO>>builder()
                        .code(1001)
                        .message("No data found")
                        .result(null)
                        .status("ERROR")
                        .build();  // Trả về lỗi 1001 nếu không có kết quả
            }

            // Chuyển đổi kết quả thành DTO
            List<TopUserDTO> topUsers = new ArrayList<>();
            for (Object[] result : results) {
                String userID = (String) result[0];  // userID
                String userName = (String) result[1]; // user_name
                String avatarUrl = (String) result[2]; // avatar_url
                int totalViewCount = (int) result[3];  // total_view_count

                topUsers.add(new TopUserDTO(userID, userName, avatarUrl, totalViewCount));
            }

            // Trả về dữ liệu thành công
            return ApiResponse.<List<TopUserDTO>>builder()
                    .code(1000)
                    .message("Top users by view count fetched successfully")
                    .result(topUsers)
                    .status("SUCCESS")
                    .build();  // Trả về DTO

        } catch (Exception e) {
            // Trả về lỗi nếu gặp ngoại lệ
            return ApiResponse.<List<TopUserDTO>>builder()
                    .code(1002)
                    .message("An error occurred: " + e.getMessage())
                    .result(null)
                    .status("ERROR")
                    .build();  // Trả về lỗi khi có ngoại lệ
        }
    }
}
