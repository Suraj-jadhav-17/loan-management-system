
export interface LoanDealRequest {
  customerId: number;
  loanType: 'HOME_LOAN' | 'CAR_LOAN';
  amount: number;
  tenureMonths: number;
  emiAmount: number;
  bankName: string;
  bankAccountNumber: string;
  ifscCode: string;
  emiDay: number;
}
