
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
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

  loanAccountId: number | null = null;
  closure: LoanClosure | null = null;
  loading = false;
  error = '';

  loadClosure(): void {
    this.error = '';
    this.closure = null;

    if (!this.loanAccountId || this.loanAccountId <= 0) {
      this.error = 'Enter a valid loan account ID.';
      return;
    }

    this.loading = true;

    this.service.getCustomerClosure(this.loanAccountId).subscribe({
      next: result => {
        this.closure = result;
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        this.error = err?.error?.message || 'Unable to load closure details.';
      }
    });
  }
}
