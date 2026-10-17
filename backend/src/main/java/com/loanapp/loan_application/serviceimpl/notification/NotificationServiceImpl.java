
package com.loanapp.loan_application.serviceimpl.notification;

import com.loanapp.loan_application.dto.notification.NotificationDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.notification.Notification;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.notification.NotificationRepository;
import com.loanapp.loan_application.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmiScheduleRepository emiScheduleRepository;

    @Override
    public List<NotificationDto> getNotifications(Long loanAccountId) {
        return notificationRepository
            .findByLoanAccountIdOrderByCreatedAtDesc(loanAccountId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    @Transactional
    public int generateNotifications(Long loanAccountId) {
        LocalDate today = LocalDate.now();
        LocalDate reminderLimit = today.plusDays(7);
        int createdCount = 0;

        List<EmiSchedules> schedules =
            emiScheduleRepository.findByLoanAccountId(loanAccountId);

        for (EmiSchedules emi : schedules) {
            if (emi.getPaymentStatus() != PaymentStatus.PENDING) {
                continue;
            }

            LocalDate dueDate = emi.getDueDate();

            if (dueDate == null || dueDate.isAfter(reminderLimit)) {
                continue;
            }

            boolean overdue = dueDate.isBefore(today);

            String type = overdue ? "OVERDUE" : "UPCOMING_EMI";

            boolean alreadyExists =
                notificationRepository
                    .existsByLoanAccountIdAndEmiScheduleIdAndNotificationType(
                        loanAccountId,
                        emi.getEmiScheduleId(),
                        type
                    );

            if (alreadyExists) {
                continue;
            }

            Notification notification = new Notification();
            notification.setLoanAccountId(loanAccountId);
            notification.setEmiScheduleId(emi.getEmiScheduleId());
            notification.setNotificationType(type);
            notification.setDueDate(dueDate);
            notification.setReadStatus(false);
            notification.setCreatedAt(LocalDateTime.now());

            if (overdue) {
                notification.setTitle("EMI Overdue");
                notification.setMessage(
                    "Your EMI installment " + emi.getInstallmentNo()
                    + " was due on " + dueDate
                    + ". Please review your pending payment."
                );
            } else {
                notification.setTitle("Upcoming EMI Due");
                notification.setMessage(
                    "Your EMI installment " + emi.getInstallmentNo()
                    + " is due on " + dueDate + "."
                );
            }

            notificationRepository.save(notification);
            createdCount++;
        }

        return createdCount;
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository
            .findById(notificationId)
            .orElseThrow(() ->
                new IllegalArgumentException("Notification not found")
            );

        notification.setReadStatus(true);

        return toDto(notificationRepository.save(notification));
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
            notification.getNotificationId(),
            notification.getLoanAccountId(),
            notification.getEmiScheduleId(),
            notification.getNotificationType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getDueDate(),
            notification.isReadStatus(),
            notification.getCreatedAt()
        );
    }
}
