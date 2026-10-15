
package com.loanapp.loan_application.service.notification;

import com.loanapp.loan_application.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getNotifications(Long loanAccountId);

    int generateNotifications(Long loanAccountId);

    NotificationDto markAsRead(Long notificationId);
}
