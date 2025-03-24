package com.example.KaizenStream_BE.dto.respone.category;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class CategoryRespone {
    String categoryId;
    String name;
    String description;
}
