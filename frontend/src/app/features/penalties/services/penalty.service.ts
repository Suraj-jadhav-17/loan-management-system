
import { computed, Service, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Penalty } from '../models/penalty.model';

@Service()
export class PenaltyService {
  private http: HttpClient;

  constructor(http: HttpClient) {
    this.http = http;
  }

  private baseUrl = 'http://localhost:8080/penalties';

  penalties = signal<Penalty[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  penaltyCount = computed(() => this.penalties().length);

  totalPenalty = computed(() =>
    this.penalties().reduce(
      (total, penalty) => total + penalty.penaltyAmount,
      0
    )
  );

  getPenaltiesByLoan(loanAccountId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<Penalty[]>(`${this.baseUrl}/loan/${loanAccountId}`)
      .subscribe({
        next: (data) => {
          this.penalties.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Unable to load penalties.');
          this.loading.set(false);
        }
      });
  }

  getPenaltiesByEmi(emiScheduleId: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .get<Penalty[]>(`${this.baseUrl}/emi/${emiScheduleId}`)
      .subscribe({
        next: (data) => {
          this.penalties.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Unable to load EMI penalties.');
          this.loading.set(false);
        }
      });
  }

  applyOverduePenalties(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.http
      .post(`${this.baseUrl}/overdue`, {})
      .subscribe({
        next: () => {
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Unable to apply overdue penalties.');
          this.loading.set(false);
        }
      });
  }
}
