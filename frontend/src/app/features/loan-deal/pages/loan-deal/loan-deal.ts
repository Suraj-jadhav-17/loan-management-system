import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cibil } from '../../../cibil/services/cibil';
import { EligibilityResult, ScoreCard } from '../../../cibil/models/cibil.model';
import { LoanDealService } from '../../service/loan-deal-service';
import { LoanDealRequest } from '../../model/loan-deal-request.model';

@Component({
  selector: 'app-loan-deal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loan-deal.html',
  styleUrl: './loan-deal.scss'
})
export class LoanDeal implements OnInit {

  private readonly cibilService = inject(Cibil);
  private readonly loanDealService = inject(LoanDealService);

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
  eligibility: EligibilityResult | null = null;
isLoadingEligibility = false;
eligibilityError = '';

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
  this.loadLatestEligibility();
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
        console.log('Latest ScoreCard:', response);
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

  loadLatestEligibility(): void {
  this.isLoadingEligibility = true;
  this.eligibilityError = '';

  this.cibilService.getLatestEligibility().subscribe({
    next: (response: EligibilityResult) => {
      this.eligibility = response;
      this.isLoadingEligibility = false;
    },
    error: (error: { status?: number }) => {
      this.eligibility = null;

      this.eligibilityError = error.status === 404
        ? 'Eligibility result not found. Please complete your eligibility assessment first.'
        : 'Unable to load your eligibility status. Please try again.';

      this.isLoadingEligibility = false;
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
    if (this.isLoadingScoreCard || this.isLoadingEligibility) {
  this.errorMessage = 'Please wait while your scorecard and eligibility are loading.';
  return false;
}

if (!this.scoreCard) {
  this.errorMessage =
    'Scorecard not generated. Please complete your CIBIL assessment first.';
  return false;
}

if (!this.eligibility) {
  this.errorMessage =
    this.eligibilityError || 'Eligibility result not found. Please complete your eligibility assessment first.';
  return false;
}

if (
  this.eligibility.eligibilityStatus !== 'ELIGIBLE'
) {
  this.errorMessage =
    this.eligibility.rejectionReason ||
    `You are not eligible to apply for a loan. Current status: ${this.eligibility.eligibilityStatus ?? 'Unknown'}.`;
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

  const request: LoanDealRequest = {
    customerId: 2,
    loanType: this.loanDeal.loanType as 'HOME_LOAN' | 'CAR_LOAN',
    amount: this.loanDeal.amount!,
    tenureMonths: this.loanDeal.tenureMonths!,
    emiAmount: 0,
    bankName: this.loanDeal.bankName.trim(),
    bankAccountNumber: this.loanDeal.bankAccountNumber.trim(),
    ifscCode: this.loanDeal.ifscCode.trim().toUpperCase(),
    emiDay: this.loanDeal.emiDay!
  };

  this.isSubmitting = true;

  this.loanDealService.createLoanDeal(request).subscribe({
    next: (response) => {
      this.successMessage = response.message || 'Loan deal created successfully.';
      this.isSubmitting = false;
      console.log('Created Loan Deal:', response.data);
    },
    error: (error) => {
      this.errorMessage =
        error.error?.message || 'Unable to create loan deal. Please try again.';
      this.isSubmitting = false;
      console.error('Loan Deal API error:', error);
    }
  });
}

}