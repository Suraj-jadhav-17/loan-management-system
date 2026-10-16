
export type LoanClosureType = 'NORMAL' | 'FORECLOSURE';

export type LoanClosureStatus =
  | 'PENDING'
  | 'CLOSED'
  | 'FAILED';

export interface LoanClosure {
  loanClosureId: number;
  loanAccountId: number;
  closureType: LoanClosureType;
  finalSettlementAmount: number;
  closureDate: string | null;
  closureStatus: LoanClosureStatus;
  closedBy: number | null;
  nocGenerated: boolean;
  cibilUpdated: boolean;
}
