package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.withdraw.WithdrawResponse;
import com.example.KaizenStream_BE.entity.Withdraw;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WithdrawMapper {

    @Mapping(source = "user.userId", target = "userId")
    WithdrawResponse toDto(Withdraw withdraw);
}
