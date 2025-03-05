package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.test.TestRequest;
import com.example.KaizenStream_BE.entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestMapper {

    @Mapping(target = "id", ignore = true)
    Test toTest(TestRequest test);

}
