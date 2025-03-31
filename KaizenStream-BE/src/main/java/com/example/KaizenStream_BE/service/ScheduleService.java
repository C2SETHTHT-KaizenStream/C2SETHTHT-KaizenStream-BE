package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.schedule.CreateScheduleRequest;
import com.example.KaizenStream_BE.dto.respone.schedule.CreateScheduleRespone;
import com.example.KaizenStream_BE.entity.Schedule;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.ScheduleMapper;
import com.example.KaizenStream_BE.repository.ScheduleRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ScheduleService {
    ScheduleRepository scheduleRepository;
    UserRepository userRepository;
    ScheduleMapper scheduleMapper;

    public CreateScheduleRespone createSchecdule(CreateScheduleRequest request) {
        User user=userRepository.findById(request.getUserId()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        Schedule schedule=scheduleMapper.toSchedule(request);
        schedule.setUser(user);
        scheduleRepository.save(schedule);
        return scheduleMapper.toCreateScheduleRespone(schedule);
    }
}
