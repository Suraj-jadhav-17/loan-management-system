
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PenaltyService } from '../../services/penalty.service';

@Component({
  selector: 'app-my-penalties',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-penalties.html',
  styleUrl: './my-penalties.scss'
})
export class MyPenalties {
  loanAccountId: number | null = null;

  constructor(public penaltyService: PenaltyService) {}

  viewPenalties(): void {
    if (
      this.loanAccountId === null ||
      !Number.isFinite(this.loanAccountId) ||
      this.loanAccountId <= 0
    ) {
      this.penaltyService.errorMessage.set(
        'Please enter a valid Loan Account ID.'
      );
      return;
    }

    this.penaltyService.getPenaltiesByLoan(this.loanAccountId);
  }

  applyOverduePenalties(): void {
    this.penaltyService.applyOverduePenalties();
  }
}
