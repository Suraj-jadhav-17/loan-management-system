
export type DisbursementStatus =
  | 'PENDING'
  | 'DISBURSED'
  | 'COMPLETED'
  | 'FAILED';

export interface DisbursementLoanDeal {
  id: number;
  customer?: {
    customerId: number;
    firstName?: string;
    lastName?: string;
  };
  loanType: string;
  amount: number;
  interestRate: number;
  tenureMonths: number;
  emiAmount: number;
  createdAt?: string;
}

export interface Disbursement {
  disbursementId: number;
  loanDeal: DisbursementLoanDeal;
  disburseAmount: number;
  bankPartner: string;
  disbursementDate: string;
  status: DisbursementStatus;
}

export interface DisbursementRequest {
  disbursementId: number;
  bankPartner: string;
  disbursementDate: string;
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

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
