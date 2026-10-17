
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';

import { LoanClosure } from '../../models/loan-closure.model';
import { LoanClosureService } from '../../services/loan-closure';

@Component({
  selector: 'app-closure-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './closure-history.html',
  styleUrl: './closure-history.scss'
})
export class ClosureHistory implements OnInit {
  private readonly loanClosureService = inject(LoanClosureService);

  closures: LoanClosure[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    if (this.loading) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.loanClosureService.getCustomerClosureHistory().subscribe({
      next: (response) => {
        this.closures = Array.isArray(response) ? response : [];
        this.loading = false;
      },
      error: (error) => {
        this.closures = [];
        this.errorMessage =
          error?.error?.message ||
          'Unable to load your loan closure history. Please try again.';
        this.loading = false;
      }
    });
  }

  trackByClosureId(index: number, closure: LoanClosure): number {
    return closure.loanClosureId;
  }
}
