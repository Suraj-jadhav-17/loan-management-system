
export type DealReviewStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'UNDER_REVIEW';

export interface DealReviewCustomer {
  customerId: number;
  firstName?: string;
  lastName?: string;
}

export interface DealReviewLoanDeal {
  id: number;
  customer?: DealReviewCustomer;
  loanType: string;
  amount: number;
  appliedDate?: string;
  createdAt?: string;
}

export interface DealReviewOfficer {
  userId: number;
  firstName?: string;
  lastName?: string;
}

export interface DealReview {
  reviewId: number;
  loanDeal: DealReviewLoanDeal;
  officer?: DealReviewOfficer | null;
  status: DealReviewStatus;
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

export interface ApiResponse<T> {
  status: string;
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
  path?: string;
  metadata?: unknown;
}
