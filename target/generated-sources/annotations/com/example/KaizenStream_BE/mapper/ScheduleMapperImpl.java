package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.schedule.CreateScheduleRequest;
import com.example.KaizenStream_BE.dto.respone.schedule.CreateScheduleRespone;
import com.example.KaizenStream_BE.entity.Schedule;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class ScheduleMapperImpl implements ScheduleMapper {

    @Override
    public CreateScheduleRespone toCreateScheduleRespone(Schedule schedule) {
        if ( schedule == null ) {
            return null;
        }

        CreateScheduleRespone.CreateScheduleResponeBuilder createScheduleRespone = CreateScheduleRespone.builder();

        createScheduleRespone.scheduleId( schedule.getScheduleId() );
        createScheduleRespone.description( schedule.getDescription() );
        createScheduleRespone.scheduleTime( schedule.getScheduleTime() );
        createScheduleRespone.status( schedule.getStatus() );

        return createScheduleRespone.build();
    }

    @Override
    public Schedule toSchedule(CreateScheduleRequest scheduleRequest) {
        if ( scheduleRequest == null ) {
            return null;
        }

        Schedule schedule = new Schedule();

        schedule.setDescription( scheduleRequest.getDescription() );
        schedule.setScheduleTime( scheduleRequest.getScheduleTime() );

        return schedule;
    }
}
