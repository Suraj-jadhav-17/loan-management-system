import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cibil } from '../../../cibil/services/cibil';
import { ScoreCard } from '../../../cibil/models/cibil.model';

@Component({
  selector: 'app-loan-deal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loan-deal.html',
  styleUrl: './loan-deal.scss'
})
export class LoanDeal implements OnInit {

  private readonly cibilService = inject(Cibil);

  readonly loanTypes = [
    { name: 'Home Loan', value: 'HOME_LOAN' },
    { name: 'Car Loan', value: 'CAR_LOAN' }
  ];

  readonly emiDays: number[] = [3, 5, 15];

  scoreCard: ScoreCard | null = null;
  eligibleLoanAmount: number | null = null;

  isLoadingScoreCard = false;
  isSubmitting = false;

  scoreCardError = '';
  errorMessage = '';
  successMessage = '';

  loanDeal = {
    loanType: '',
    amount: null as number | null,
    tenureMonths: null as number | null,
    emiAmount: null as number | null,
    bankName: '',
    bankAccountNumber: '',
    ifscCode: '',
    emiDay: null as number | null
  };

  ngOnInit(): void {
    this.loadLatestScoreCard();
  }

  get interestRate(): number | null {
    switch (this.loanDeal.loanType) {
      case 'CAR_LOAN':
        return 10;
      case 'HOME_LOAN':
        return 15;
      default:
        return null;
    }
  }

  loadLatestScoreCard(): void {
    this.isLoadingScoreCard = true;
    this.scoreCardError = '';

    this.cibilService.getLatestScoreCard().subscribe({
      next: (response: ScoreCard) => {
        this.scoreCard = response;
        this.eligibleLoanAmount = response.eligibleLoanAmount;
        this.isLoadingScoreCard = false;
      },
      error: (error: { status?: number }) => {
        this.scoreCard = null;
        this.eligibleLoanAmount = null;

        this.scoreCardError =
          error.status === 404
            ? 'No scorecard found. Please complete your CIBIL assessment first.'
            : 'Unable to load your scorecard. Please try again.';

        this.isLoadingScoreCard = false;
      }
    });
  }

  onLoanDetailsChange(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  validateForm(): boolean {
    const amount = this.loanDeal.amount;
    const tenure = this.loanDeal.tenureMonths;
    const emiDay = this.loanDeal.emiDay;
    const bankName = this.loanDeal.bankName.trim();
    const accountNumber = this.loanDeal.bankAccountNumber.trim();
    const ifscCode = this.loanDeal.ifscCode.trim().toUpperCase();

    if (!this.loanDeal.loanType) {
      this.errorMessage = 'Please select a loan type.';
      return false;
    }

    if (
      amount === null ||
      !Number.isFinite(amount) ||
      amount <= 0
    ) {
      this.errorMessage = 'Enter a valid loan amount.';
      return false;
    }

    if (this.eligibleLoanAmount === null) {
      this.errorMessage = 'Your eligible loan amount is not available yet.';
      return false;
    }

    if (amount > this.eligibleLoanAmount) {
      this.errorMessage =
        'Requested amount cannot exceed your eligible loan amount.';
      return false;
    }

    if (
      tenure === null ||
      !Number.isInteger(tenure) ||
      tenure <= 0
    ) {
      this.errorMessage = 'Enter a valid tenure in months.';
      return false;
    }

    if (!bankName) {
      this.errorMessage = 'Please enter the bank name.';
      return false;
    }

    if (!/^\d{9,18}$/.test(accountNumber)) {
      this.errorMessage =
        'Bank account number must contain 9 to 18 digits.';
      return false;
    }

    if (!/^[A-Z]{4}0[A-Z0-9]{6}$/.test(ifscCode)) {
      this.errorMessage = 'Please enter a valid IFSC code.';
      return false;
    }

    if (
      emiDay === null ||
      !Number.isInteger(emiDay) ||
      emiDay < 1 ||
      emiDay > 28
    ) {
      this.errorMessage = 'EMI day must be between 1 and 28.';
      return false;
    }

    this.errorMessage = '';
    return true;
  }

  submitLoanDeal(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isSubmitting || !this.validateForm()) {
      return;
    }

    const request = {
      loanType: this.loanDeal.loanType,
      amount: this.loanDeal.amount,
      tenureMonths: this.loanDeal.tenureMonths,
      bankName: this.loanDeal.bankName.trim(),
      bankAccountNumber: this.loanDeal.bankAccountNumber.trim(),
      ifscCode: this.loanDeal.ifscCode.trim().toUpperCase(),
      emiDay: this.loanDeal.emiDay
    };

    console.log('Validated Loan Deal request:', request);

    this.successMessage =
      'Form validation successful. Backend submission is not connected yet.';
  }
}