package com.example.KaizenStream_BE.dto.respone.chart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonationGrowthDTO {
    double percentage;
    double amount;
}
