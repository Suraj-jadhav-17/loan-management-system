
package com.loanapp.loan_application.controller.notification;

import com.loanapp.loan_application.dto.notification.NotificationDto;
import com.loanapp.loan_application.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/loan/{loanAccountId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @PathVariable Long loanAccountId) {

        return ResponseEntity.ok(
            notificationService.getNotifications(loanAccountId)
        );
    }

    @PostMapping("/generate/{loanAccountId}")
    public ResponseEntity<Map<String, Object>> generateNotifications(
            @PathVariable Long loanAccountId) {

        int count =
            notificationService.generateNotifications(loanAccountId);

        return ResponseEntity.ok(Map.of(
            "message", "Notification generation completed",
            "createdCount", count
        ));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
            notificationService.markAsRead(notificationId)
        );
    }
}
