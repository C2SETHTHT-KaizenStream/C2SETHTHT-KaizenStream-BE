package com.example.KaizenStream_BE.dto.request.livestream;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class CreateLivestreamRequest {

    @NotEmpty(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @NotEmpty(message = "Thumbnail URL is required")
    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "Invalid thumbnail URL format")
    private String thumbnail;

    @Min(value = 0, message = "Viewer count must be a positive integer")
    private int viewerCount;

    @NotNull(message = "Start time is required")
    private Date startTime;


    private Date endTime;

    @NotEmpty(message = "Status is required")

    @Pattern(regexp = "^(active|inactive|ended)$", message = "Status must be one of: active, inactive, ended")
    private String status;

    private  String userId;

}
