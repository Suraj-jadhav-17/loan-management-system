
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class Notifications {
  loanAccountId: number | null = null;

  constructor(public notificationService: NotificationService) {}

  loadNotifications(): void {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.notificationService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return;
    }

    this.notificationService.getNotifications(this.loanAccountId);
  }

  generateNotifications(): void {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.notificationService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return;
    }

    this.notificationService.generateNotifications(this.loanAccountId);
  }

  markAsRead(notificationId: number): void {
    this.notificationService.markAsRead(notificationId);
  }
}
