
import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { MultipleEmiRequest } from '../models/multiple-emi.model';

@Injectable({
  providedIn: 'root'
})
export class MultipleEmiService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/multiple-emi';

  requests = signal<MultipleEmiRequest[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  requestCount = computed(() => this.requests().length);

  loadRequests(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<MultipleEmiRequest[]>(
        `${this.baseUrl}/loan/${loanAccountId}`
      )
      .subscribe({
        next: (data: MultipleEmiRequest[]) => {
          this.requests.set(data);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable. Check whether it is running.'
              : `Unable to load requests. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }

  submitRequest(
    data: MultipleEmiRequest,
    onSuccess?: () => void
  ): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .post(`${this.baseUrl}/request`, data)
      .subscribe({
        next: () => {
          onSuccess?.();
          this.loadRequests(data.loanAccountId);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable. Check whether it is running.'
              : `Unable to submit request. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }

  approveRequest(
    requestId: number,
    loanAccountId: number
  ): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .put(`${this.baseUrl}/approve/${requestId}`, {})
      .subscribe({
        next: () => {
          this.loadRequests(loanAccountId);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage.set(
            error.status === 0
              ? 'Backend is not reachable. Check whether it is running.'
              : `Unable to approve request. HTTP ${error.status}`
          );
          this.loading.set(false);
        }
      });
  }
}
