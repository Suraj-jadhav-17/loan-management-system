
import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { EmiSchedule } from '../models/emi-schedule.model';

@Injectable({
  providedIn: 'root'
})
export class EmiSchedulerService {
  private http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080/emi';

  schedules = signal<EmiSchedule[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  paidCount = computed(
    () =>
      this.schedules().filter(
        schedule => schedule.paymentStatus?.toUpperCase() === 'PAID'
      ).length
  );

  pendingCount = computed(
    () =>
      this.schedules().filter(
        schedule => schedule.paymentStatus?.toUpperCase() !== 'PAID'
      ).length
  );

  getSchedule(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<EmiSchedule[]>(`${this.baseUrl}/${loanAccountId}`)
      .subscribe({
        next: (data: EmiSchedule[]) => {
          this.schedules.set(data);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable.'
              : `Unable to load EMI schedule. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }

  generateSchedule(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .post(`${this.baseUrl}/generate/${loanAccountId}`, {})
      .subscribe({
        next: () => {
          this.getSchedule(loanAccountId);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable.'
              : `Unable to generate EMI schedule. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }
}
