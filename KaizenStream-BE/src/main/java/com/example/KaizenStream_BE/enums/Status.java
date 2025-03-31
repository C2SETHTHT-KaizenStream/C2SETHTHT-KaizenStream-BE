package com.example.KaizenStream_BE.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    ACTIVE("active"), INACTIVE("inactive"), ENDED("ended"), PENDING("pending");

   String description;
}
