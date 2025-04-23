package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.SuggestionRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;

import com.example.KaizenStream_BE.dto.respone.suggestion.SuggestionResponse;
import com.example.KaizenStream_BE.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suggestions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuggestionController {
    private final SuggestionService suggestionService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<SuggestionResponse>>> getSuggestions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") @Valid Integer limit) {
        SuggestionRequest request = SuggestionRequest.builder()
                .userId(userId)
                .limit(limit)
                .build();

        List<SuggestionResponse> suggestions = suggestionService.getSuggestions(request);

        // Debug log
        suggestions.forEach(s -> System.out.println("Suggest: " + s.getStreamerName() + " - Score: " + s.getScore()));
        suggestions.forEach(s -> System.out.println("Endtime:" + s.getEndTime()));

        return ResponseEntity.ok(ApiResponse.<List<SuggestionResponse>>builder()
                .result(suggestions)
                .message("Suggestions retrieved successfully")
                .status("SUCCESS")
                .build());
    }

}
