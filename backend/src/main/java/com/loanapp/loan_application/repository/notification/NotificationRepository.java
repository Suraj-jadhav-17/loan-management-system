
package com.loanapp.loan_application.repository.notification;

import com.loanapp.loan_application.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByLoanAccountIdOrderByCreatedAtDesc(
        Long loanAccountId
    );

    boolean existsByLoanAccountIdAndEmiScheduleIdAndNotificationType(
        Long loanAccountId,
        Long emiScheduleId,
        String notificationType
    );
}
