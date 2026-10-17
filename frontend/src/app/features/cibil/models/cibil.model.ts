
export interface ScoreCard {
  scoreCardId: number;
  customerId: number;
  currentStatus: string;
  rejectionReason: string | null;
  appliedDate: string | null;
  cibilScore: number | null;
  riskCategory: string | null;
  eligibleLoanAmount: number | null;
  foir: number | null;
  totalScore: number | null;
}

export interface EligibilityResult {
  id: number;
  customerId: number;
  cibilScore: number | null;
  eligibilityStatus: string | null;
  decision: string | null;
  riskCategory: string | null;
  borrowingLimit: string | null;
  loanAmount: number | null;
  rejectionReason: string | null;
}

export interface CibilReport {
  id: number;
  customerId: number;
  panNo: string | null;
  cibilScore: number | null;
  checkDate: string | null;
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
