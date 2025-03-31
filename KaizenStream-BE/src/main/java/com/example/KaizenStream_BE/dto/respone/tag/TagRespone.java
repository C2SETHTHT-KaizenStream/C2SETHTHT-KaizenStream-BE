package com.example.KaizenStream_BE.dto.respone.tag;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class TagRespone {
    String tagId;
    String name;
}
