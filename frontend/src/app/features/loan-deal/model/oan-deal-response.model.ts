
export interface LoanDealCustomer {
  customerId: number;
}

export interface LoanDealResponse {
  id: number;
  customer: LoanDealCustomer;
  loanType: 'HOME_LOAN' | 'CAR_LOAN';
  amount: number;
  interestRate: number;
  tenureMonths: number;
  emiAmount: number;
  bankName: string;
  bankAccountNumber: string;
  ifscCode: string;
  emiDay: number;
  approvedAmount: number;
}

export interface ApiResponse<T> {
  status: string;
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
  path?: string;
  metadata?: unknown;
}
