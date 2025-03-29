package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.schedule.CreateScheduleRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.schedule.CreateScheduleRespone;
import com.example.KaizenStream_BE.mapper.ScheduleMapper;
import com.example.KaizenStream_BE.service.ScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/schedule")
public class ScheduleController {
    ScheduleService scheduleService;
    ScheduleMapper scheduleMapper;
    @PostMapping
    ApiResponse<CreateScheduleRespone> createSchedule(@RequestBody CreateScheduleRequest request){
        return ApiResponse.<CreateScheduleRespone>builder().result(scheduleService.createSchecdule(request)).build();
    }
}
