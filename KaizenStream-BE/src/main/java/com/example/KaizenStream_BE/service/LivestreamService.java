package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LivestreamService {
    LivestreamRepository livestreamRepository;
    LivestreamMapper livestreamMapper;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    ScheduleRepository scheduleRepository;
    TagRepository tagRepository;
    public LivestreamRespone createLivestream( CreateLivestreamRequest request) {
        User user=userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        List<Category> categoryEntities = request.getCategories().stream()
                .map(name -> {
                    return categoryRepository.findFirstByNameIgnoreCase(name)
                            .orElseGet(() -> {
                                Category category=new Category();
                                category.setName(name);
                                categoryRepository.save(category);
                                return category;
                            });
                })
                .collect(Collectors.toList());
        List<Tag>  tags=request.getTags().stream().map(name->{
            return  tagRepository.findFirstByNameIgnoreCase(name)
                    .orElseGet(()->{
                        Tag tag=new Tag();
                        tag.setName(name);
                        tagRepository.save(tag);
                        return tag;
                    });
        }).collect(Collectors.toList());
        Livestream  livestream= livestreamMapper.toLivestream(request);
        livestream.setCategories(categoryEntities);
        livestream.setTags(tags);
        livestream.setUser(user);

        if (request.getScheduleId() != null && !request.getScheduleId().isEmpty()) {
            Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXIST));
            livestream.setSchedule(schedule);
            if(schedule.getScheduleTime().after(new Date())) {
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
        LivestreamRespone livestreamRespone=livestreamMapper.toLivestreamRespone(livestream);
        List<String> categoryList=livestream.getCategories().stream().map(c->c.getName()).toList();
        List<String> tagList=livestream.getTags().stream().map(c->c.getName()).toList();
        livestreamRespone.setCategories(categoryList);
        livestreamRespone.setTags(tagList);
        return livestreamRespone;
    }

    public LivestreamRespone getLivestreamById(String id) {
        return livestreamMapper.toLivestreamRespone(livestreamRepository.findById(id).orElseThrow(()->new RuntimeException("LIVESTREAM_NOT_EXITS")));
    }

    public List<LivestreamRespone> getAll() {
        var live=livestreamRepository.findAll();

        return live.stream().map(livestream->{
            LivestreamRespone livestreamRespone=livestreamMapper.toLivestreamRespone(livestream);
            List<String> categoryList=livestream.getCategories().stream().map(c->c.getName()).toList();
            List<String> tagList=livestream.getTags().stream().map(c->c.getName()).toList();
            livestreamRespone.setCategories(categoryList);
            livestreamRespone.setTags(tagList);
            return  livestreamRespone;
        }).toList();
    }


    public LivestreamRespone updateLivestreamById( @Valid UpdateLivestreamRequest updateLivestreamRequest) {
        var live=livestreamRepository.findById(updateLivestreamRequest.getLivestreamId()).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        livestreamMapper.updateLivestream(live,updateLivestreamRequest);
        livestreamRepository.save(live);
        return livestreamMapper.toLivestreamRespone(live);
    }


    public String deleteById(String id) {
        var live=livestreamRepository.findById(id).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));

        livestreamRepository.deleteById(id);
        return "Livestream has been deleted";
    }

    public void updateStatus(String streamId, Status status) {
        var live=livestreamRepository.findById(streamId).orElseThrow(()-> new RuntimeException("LIVESTREAM_NOT_EXITS"));
        live.setStatus(status.getDescription());
        log.warn("updateStatus: "+live.getStatus());
        System.out.println("updateStatus:updateStatus:"+live.getStatus());
        livestreamRepository.save(live);
    }

    public Page<LivestreamRespone> getAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Livestream> livePage = livestreamRepository.findAll(pageable);

        return livePage.map(livestream -> {
            System.out.println("Viewer count: "+livestream.getViewerCount());
            LivestreamRespone response = livestreamMapper.toLivestreamRespone(livestream);
            response.setCategories(livestream.getCategories().stream().map(c -> c.getName()).toList());
            response.setTags(livestream.getTags().stream().map(t -> t.getName()).toList());
            return response;
        });
    }

}
