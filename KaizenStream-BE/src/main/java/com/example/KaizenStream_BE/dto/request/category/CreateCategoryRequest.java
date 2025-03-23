package com.example.KaizenStream_BE.dto.request.category;

import jakarta.persistence.Column;
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
public class CreateCategoryRequest {

    @NotEmpty(message = "Name is required")
    String name;
    @NotEmpty(message = "Description is required")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")

    String description;
}
