
package com.loanapp.loan_application.dto.notification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NotificationDto(
    Long notificationId,
    Long loanAccountId,
    Long emiScheduleId,
    String notificationType,
    String title,
    String message,
    LocalDate dueDate,
    boolean readStatus,
    LocalDateTime createdAt
) {}
