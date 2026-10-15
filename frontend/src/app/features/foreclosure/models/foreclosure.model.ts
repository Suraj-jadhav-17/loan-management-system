
export type ForeClosureType = 'FULL' | 'PARTIAL';

export type ForeClosureStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'PAID'
  | 'CANCELLED';

export interface ForeClosureCreateRequest {
  loanAccountId: number;
  foreClosureType: ForeClosureType;
  partialAmount: number | null;
}

export interface ForeClosureResponse {
  foreClosureId: number;
  loanAccountId: number;
  foreClosureType: ForeClosureType;
  foreClosureAmount: number;
  partialAmount: number | null;
  outstandingPrincipal: number;
  accruedInterest: number;
  overdueDues: number;
  foreclosureFee: number;
  status: ForeClosureStatus;
  isPaid: boolean;
  requestedAt: string;
  approvedAt: string | null;
  closedBy: number | null;
  paymentDate: string | null;
  remarks: string | null;
}

export interface ForeClosurePayment {
  foreClosureId: number;
  paidAmount: number;
  orderId: string;
  paymentId: string;
  signature: string;
}

export interface ForeClosureApproval {
  foreClosureId: number;
  officerId?: number;
  remarks?: string;
}
