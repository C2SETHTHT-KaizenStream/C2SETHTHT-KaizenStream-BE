package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.donation.DonationRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.service.DonationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DonationController {
    DonationService donationService;

    @PostMapping("/donate")
    public ResponseEntity<ApiResponse<String>> donate(@RequestBody @Valid DonationRequest requestDTO) {

        donationService.donate(requestDTO);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(1000)
                .message("Donating successfully !")
                .result(null)
                .build();

        return ResponseEntity.ok(response);
    }


}
