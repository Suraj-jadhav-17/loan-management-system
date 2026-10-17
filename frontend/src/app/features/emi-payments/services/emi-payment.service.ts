
import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { EmiPayment } from '../models/emi-payment.model';

@Injectable({
  providedIn: 'root'
})
export class EmiPaymentService {
  private http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080';

  payments = signal<EmiPayment[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  paymentCount = computed(() => this.payments().length);

  totalPaid = computed(() =>
    this.payments()
      .filter(payment => payment.status?.toUpperCase() === 'PAID')
      .reduce((total, payment) => total + payment.totalAmount, 0)
  );

  getPayments(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<EmiPayment[]>(`${this.baseUrl}/payments/${loanAccountId}`)
      .subscribe({
        next: (data: EmiPayment[]) => {
          this.payments.set(data);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable.'
              : `Unable to load payments. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }

  makePayment(payment: EmiPayment): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .post(`${this.baseUrl}/payments`, payment)
      .subscribe({
        next: () => {
          this.getPayments(payment.loanAccountId);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable.'
              : `Unable to process payment. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }
}
