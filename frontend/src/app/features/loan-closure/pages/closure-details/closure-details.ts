import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { LoanClosureService } from '../../services/loan-closure';
import { LoanClosure } from '../../models/loan-closure.model';

@Component({
  selector: 'app-closure-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './closure-details.html',
  styleUrl: './closure-details.scss'
})
export class ClosureDetailsPage {
  private readonly service = inject(LoanClosureService);
  private readonly router = inject(Router);

  loanAccountId: number | null = null;
  closure: LoanClosure | null = null;
  loading = false;
  error = '';

  private get isLoanOfficer(): boolean {
    return this.router.url.startsWith('/loan-officer/');
  }

  loadClosure(): void {
    if (this.loading) {
      return;
    }

    this.error = '';
    this.closure = null;

    const id = Number(this.loanAccountId);

    if (
      this.loanAccountId === null ||
      !Number.isInteger(id) ||
      id <= 0
    ) {
      this.error = 'Enter a valid loan account ID.';
      return;
    }

    this.loading = true;

    const request$ = this.isLoanOfficer
      ? this.service.getOfficerClosure(id)
      : this.service.getCustomerClosure(id);

    request$.subscribe({
      next: (result) => {
        this.closure = result;
        this.loading = false;
      },
      error: (err) => {
        this.error =
          err?.error?.message || 'Unable to load closure details.';
        this.loading = false;
      }
    });
  }
}
