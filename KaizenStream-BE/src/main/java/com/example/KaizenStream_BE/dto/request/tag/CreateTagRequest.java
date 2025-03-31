package com.example.KaizenStream_BE.dto.request.tag;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class CreateTagRequest {
    @NotEmpty(message = "Name is required")
    String name;


}
