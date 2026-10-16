
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-loan-deal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './loan-deal.html',
  styleUrl: './loan-deal.scss'
})
export class LoanDeal {

  loanTypes = [
    { name: 'Home Loan', value: 'HOME_LOAN' },
    { name: 'Car Loan', value: 'CAR_LOAN' }
  ];

  eligibleLoanAmount: number | null = null;

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

  errorMessage = '';
  successMessage = '';

  get interestRate(): number | null {
    if (this.loanDeal.loanType === 'CAR_LOAN') {
      return 10;
    }

    if (this.loanDeal.loanType === 'HOME_LOAN') {
      return 15;
    }

    return null;
  }

  onLoanDetailsChange(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  validateForm(): boolean {
    if (!this.loanDeal.loanType) {
      this.errorMessage = 'Please select a loan type.';
      return false;
    }

    if (
      this.loanDeal.amount === null ||
      !Number.isFinite(this.loanDeal.amount) ||
      this.loanDeal.amount <= 0
    ) {
      this.errorMessage = 'Enter a valid loan amount.';
      return false;
    }

    if (
      this.eligibleLoanAmount === null ||
      this.loanDeal.amount > this.eligibleLoanAmount
    ) {
      this.errorMessage =
        this.eligibleLoanAmount === null
          ? 'Your eligible loan amount is not available yet.'
          : 'Requested amount cannot exceed your eligible loan amount.';
      return false;
    }

    if (
      this.loanDeal.tenureMonths === null ||
      !Number.isInteger(this.loanDeal.tenureMonths) ||
      this.loanDeal.tenureMonths <= 0
    ) {
      this.errorMessage = 'Enter a valid tenure in months.';
      return false;
    }

    if (!this.loanDeal.bankName.trim()) {
      this.errorMessage = 'Please enter the bank name.';
      return false;
    }

    if (!/^\d{9,18}$/.test(this.loanDeal.bankAccountNumber.trim())) {
      this.errorMessage =
        'Bank account number must contain 9 to 18 digits.';
      return false;
    }

    if (
      !/^[A-Z]{4}0[A-Z0-9]{6}$/.test(
        this.loanDeal.ifscCode.trim().toUpperCase()
      )
    ) {
      this.errorMessage = 'Please enter a valid IFSC code.';
      return false;
    }

    if (
      this.loanDeal.emiDay === null ||
      !Number.isInteger(this.loanDeal.emiDay) ||
      this.loanDeal.emiDay < 1 ||
      this.loanDeal.emiDay > 28
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

    if (!this.validateForm()) {
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

    console.log('Loan Deal request prepared:', request);

    this.successMessage =
      'Form validated. Backend API connection is the next step.';
  }
}
