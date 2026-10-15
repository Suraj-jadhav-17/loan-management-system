
export interface Penalty {
  penaltyId?: number;
  loanAccountId: number;
  emiScheduleId: number;
  penaltyType: string;
  penaltyAmount: number;
  delayDays: number;
  appliedDate: string;
  status: string;
}
