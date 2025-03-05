package com.example.KaizenStream_BE.dto.respone.test;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestRespone {
    String id;
    String name;
    int number;
    String des;
}
