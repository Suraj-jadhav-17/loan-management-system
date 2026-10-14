package com.loanapp.loan_application.controller.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.loan.DealReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/deal-review")
@RequiredArgsConstructor
public class DealReviewController {

    private final DealReviewService dealReviewService;

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<DealReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody DealReviewRequest request) {

        DealReviewResponse data =
                dealReviewService.updateReview(reviewId, request);

        ApiResponse<DealReviewResponse> response = createApiResponse(
                data,
                "Deal review updated successfully",
                null,
                "/v1/deal-review/" + reviewId,
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DealReviewResponse>>> getReviews(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<DealReviewResponse> data =
                dealReviewService.getReviews(null, page, size);

        ApiResponse<Page<DealReviewResponse>> response = createApiResponse(
                data,
                "Deal reviews fetched successfully",
                null,
                "/v1/deal-review",
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<DealReviewResponse>>> getReviewsByStatus(
            @PathVariable DealReviewStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<DealReviewResponse> data =
                dealReviewService.getReviewsBytStatus(status, page, size);

        ApiResponse<Page<DealReviewResponse>> response = createApiResponse(
                data,
                "Deal reviews fetched successfully",
                null,
                "/v1/deal-review/status/" + status,
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }

    private <T> ApiResponse<T> createApiResponse(
            T data,
            String message,
            Object metadata,
            String path,
            String status,
            boolean success) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(data);
        apiResponse.setMessage(message);
        apiResponse.setMetadata(metadata);
        apiResponse.setPath(path);
        apiResponse.setStatus(status);
        apiResponse.setSuccess(success);
        apiResponse.setTimestamp(LocalDateTime.now());

        return apiResponse;
    }
}
