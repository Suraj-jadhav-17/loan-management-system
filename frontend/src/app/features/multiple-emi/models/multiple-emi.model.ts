
export interface MultipleEmiRequest {
  requestId?: number;
  loanAccountId: number;
  emiScheduleId?: number;
  requestedAmount: number;
  reason?: string;
  status?: string;
  createdAt?: string;
}
