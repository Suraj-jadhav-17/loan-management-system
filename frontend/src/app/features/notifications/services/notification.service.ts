
import { computed, Service, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Notification } from '../models/notification.model';

@Service()
export class NotificationService {
  private http: HttpClient;

  constructor(http: HttpClient) {
    this.http = http;
  }

  private baseUrl = 'http://localhost:8080/notifications';

  notifications = signal<Notification[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  notificationCount = computed(() => this.notifications().length);

  unreadCount = computed(
    () => this.notifications().filter(
      notification => !notification.readStatus
    ).length
  );

  getNotifications(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<Notification[]>(`${this.baseUrl}/loan/${loanAccountId}`)
      .subscribe({
        next: (data) => {
          this.notifications.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Unable to load notifications.');
          this.loading.set(false);
        }
      });
  }

  generateNotifications(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .post<{ message: string; createdCount: number }>(
        `${this.baseUrl}/generate/${loanAccountId}`,
        {}
      )
      .subscribe({
        next: () => {
          this.getNotifications(loanAccountId);
        },
        error: () => {
          this.errorMessage.set('Unable to generate notifications.');
          this.loading.set(false);
        }
      });
  }

  markAsRead(notificationId: number): void {
    this.http
      .patch<Notification>(
        `${this.baseUrl}/${notificationId}/read`,
        {}
      )
      .subscribe({
        next: (updatedNotification) => {
          this.notifications.update(items =>
            items.map(item =>
              item.notificationId === notificationId
                ? updatedNotification
                : item
            )
          );
        },
        error: () => {
          this.errorMessage.set('Unable to mark notification as read.');
        }
      });
  }
}
