import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Foreclosure } from '../../services/foreclosure';
import {
  ForeClosureResponse,
  ForeClosureType
} from '../../models/foreclosure.model';

@Component({
  selector: 'app-foreclosure-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './foreclosure-request.html',
  styleUrl: './foreclosure-request.scss'
})
export class ForeclosureRequestPage {
  private readonly foreclosure = inject(Foreclosure);

  loanAccountId: number | null = null;
  type: ForeClosureType = 'FULL';
  partialAmount: number | null = null;
  requestId: number | null = null;

  request: ForeClosureResponse | null = null;
  loading = false;
  error = '';
  message = '';

  submitRequest(): void {
    if (this.loading) {
      return;
    }

    this.error = '';
    this.message = '';
    this.request = null;

    if (
      this.loanAccountId === null ||
      !Number.isInteger(Number(this.loanAccountId)) ||
      this.loanAccountId <= 0
    ) {
      this.error = 'Enter a valid loan account ID.';
      return;
    }

    if (
      this.type === 'PARTIAL' &&
      (this.partialAmount === null || this.partialAmount <= 0)
    ) {
      this.error = 'Enter a valid partial foreclosure amount.';
      return;
    }

    this.loading = true;

    this.foreclosure.createRequest({
      loanAccountId: Number(this.loanAccountId),
      foreClosureType: this.type,
      partialAmount: this.type === 'PARTIAL' ? this.partialAmount : null
    }).subscribe({
      next: (result) => {
        this.request = result;
        this.requestId = result.foreClosureId;
        this.loading = false;
        this.message = 'Foreclosure request submitted successfully.';
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to submit foreclosure request.';
      }
    });
  }

  loadRequest(): void {
    if (this.loading) {
      return;
    }

    this.error = '';
    this.message = '';
    this.request = null;

    if (
      this.requestId === null ||
      !Number.isInteger(Number(this.requestId)) ||
      this.requestId <= 0
    ) {
      this.error = 'Enter a valid foreclosure request ID.';
      return;
    }

    this.loading = true;

    this.foreclosure.getCustomerRequest(Number(this.requestId)).subscribe({
      next: (result) => {
        this.request = result;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to load foreclosure request.';
      }
    });
  }
}
