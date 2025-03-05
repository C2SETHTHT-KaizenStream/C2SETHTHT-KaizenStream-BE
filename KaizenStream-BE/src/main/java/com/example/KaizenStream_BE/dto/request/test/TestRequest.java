package com.example.KaizenStream_BE.dto.request.test;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class TestRequest {


    String name;
    int number;
    String des;
}
