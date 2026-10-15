
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EmiPaymentService } from '../../services/emi-payment.service';
import { EmiPayment } from '../../models/emi-payment.model';

@Component({
  selector: 'app-emi-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './emi-payments.html',
  styleUrl: './emi-payments.scss'
})
export class EmiPayments {
  loanAccountId: number | null = null;
  successMessage = '';

  constructor(public paymentService: EmiPaymentService) {}

  get payments(): EmiPayment[] {
    return this.paymentService.payments();
  }

  viewPayments(): void {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.paymentService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return;
    }

    this.successMessage = '';
    this.paymentService.getPayments(this.loanAccountId);
  }

  payNow(payment: EmiPayment): void {
    this.paymentService.errorMessage.set(
      'Payment checkout is not connected yet. Razorpay order creation and verification are required.'
    );
  }
}
