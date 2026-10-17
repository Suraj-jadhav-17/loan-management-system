
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MultipleEmiService } from '../../services/multiple-emi.service';
import { MultipleEmiRequest } from '../../models/multiple-emi.model';

@Component({
  selector: 'app-multiple-emi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './multiple-emi.html',
  styleUrl: './multiple-emi.scss'
})
export class MultipleEmi {
  loanAccountId: number | null = null;
  requestedAmount: number | null = null;
  reason = '';
  successMessage = '';

  constructor(public multipleEmiService: MultipleEmiService) {}

  get requests(): MultipleEmiRequest[] {
    return this.multipleEmiService.requests();
  }

  get loading(): boolean {
    return this.multipleEmiService.loading();
  }

  get errorMessage(): string {
    return this.multipleEmiService.errorMessage();
  }

  viewRequests(): void {
    if (!this.validateLoanAccountId()) {
      return;
    }

    this.successMessage = '';
    this.multipleEmiService.loadRequests(this.loanAccountId!);
  }

  submitRequest(): void {
    if (!this.validateLoanAccountId()) {
      return;
    }

    if (
      this.requestedAmount === null ||
      !Number.isFinite(this.requestedAmount) ||
      this.requestedAmount <= 0
    ) {
      this.multipleEmiService.errorMessage.set(
        'Please enter a valid requested amount.'
      );
      return;
    }

    const request: MultipleEmiRequest = {
      loanAccountId: this.loanAccountId!,
      requestedAmount: this.requestedAmount,
      reason: this.reason.trim()
    };

    this.successMessage = '';

    this.multipleEmiService.submitRequest(request, () => {
      this.successMessage = 'Multiple EMI request submitted successfully.';
      this.requestedAmount = null;
      this.reason = '';
    });
  }

  private validateLoanAccountId(): boolean {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.multipleEmiService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return false;
    }

    this.multipleEmiService.errorMessage.set('');
    return true;
  }
}
