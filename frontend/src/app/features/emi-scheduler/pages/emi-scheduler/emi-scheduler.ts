
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EmiSchedulerService } from '../../services/emi-scheduler.service';
import { EmiSchedule } from '../../models/emi-schedule.model';

@Component({
  selector: 'app-emi-scheduler',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './emi-scheduler.html',
  styleUrl: './emi-scheduler.scss'
})
export class EmiSchedulerComponent {
  loanAccountId: number | null = null;
  successMessage = '';

  constructor(public emiService: EmiSchedulerService) {}

  get schedules(): EmiSchedule[] {
    return this.emiService.schedules();
  }

  get paidSchedules(): EmiSchedule[] {
    return this.schedules.filter(
      item => item.paymentStatus?.toUpperCase() === 'PAID'
    );
  }

  get pendingSchedules(): EmiSchedule[] {
    return this.schedules.filter(
      item => item.paymentStatus?.toUpperCase() !== 'PAID'
    );
  }

  get loading(): boolean {
    return this.emiService.loading();
  }

  get errorMessage(): string {
    return this.emiService.errorMessage();
  }

  toNumber(value: number | string | null | undefined): number {
    return Number(value || 0);
  }

  get paidAmount(): number {
    return this.paidSchedules.reduce(
      (sum, item) => sum + this.toNumber(item.emi),
      0
    );
  }

  get pendingAmount(): number {
    return this.pendingSchedules.reduce(
      (sum, item) => sum + this.toNumber(item.emi),
      0
    );
  }

  get remainingBalance(): number {
    return this.pendingAmount;
  }

  viewSchedule(): void {
    if (!this.validateLoanId()) {
      return;
    }

    this.successMessage = '';
    this.emiService.getSchedule(this.loanAccountId!);
  }

  generateSchedule(): void {
    if (!this.validateLoanId()) {
      return;
    }

    this.successMessage = '';
    this.emiService.generateSchedule(this.loanAccountId!);
  }

  private validateLoanId(): boolean {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.emiService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return false;
    }

    this.emiService.errorMessage.set('');
    return true;
  }
}
