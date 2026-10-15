package com.loanapp.loan_application.entity.notification;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
    name = "notifications",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"loanAccountId", "emiScheduleId", "notificationType"}
    )
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long loanAccountId;

    @Column(nullable = false)
    private Long emiScheduleId;

    @Column(nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean readStatus = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
