package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.schedule.CreateScheduleRequest;
import com.example.KaizenStream_BE.dto.respone.schedule.CreateScheduleRespone;
import com.example.KaizenStream_BE.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ScheduleMapper {


    CreateScheduleRespone toCreateScheduleRespone(Schedule schedule);

    @Mapping(target = "user", ignore = true) // Chúng ta sẽ set user trong service hoặc controller
    @Mapping(target = "status", ignore = true) // Chúng ta sẽ set user trong service hoặc controller
    Schedule toSchedule(CreateScheduleRequest scheduleRequest);
}
