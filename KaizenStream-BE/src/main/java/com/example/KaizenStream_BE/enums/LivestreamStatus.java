package com.example.KaizenStream_BE.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LivestreamStatus {
    ACTIVE("active"), INACTIVE("inactive"), ENDED("ended");

   String description;
}
