package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.entity.Livestream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface  LivestreamMapper {

    LivestreamRespone toLivestreamRespone(Livestream livestream);


    @Mapping(target = "livestreamId", ignore = true) // Vì livestreamId sẽ được tự động tạo
    @Mapping(target = "user", ignore = true) // Chúng ta sẽ set user trong service hoặc controller
    Livestream toLivestream(CreateLivestreamRequest livestream);

    @Mapping (target ="user", ignore = true)
    void  updateLivestream(@MappingTarget Livestream livestream, UpdateLivestreamRequest request);

}
