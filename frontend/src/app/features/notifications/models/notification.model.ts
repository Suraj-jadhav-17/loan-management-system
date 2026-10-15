
export interface Notification {
  notificationId: number;
  loanAccountId: number;
  emiScheduleId: number;
  notificationType: string;
  title: string;
  message: string;
  dueDate: string | null;
  readStatus: boolean;
  createdAt: string;
}
