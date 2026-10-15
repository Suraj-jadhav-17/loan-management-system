
export interface EmiSchedule {
  id?: number;
  emiScheduleId?: number;
  loanAccountId: number;
  installmentNo: number;
  dueDate: string;
  principalAmount: number;
  interestAmount: number;
  openingBalance: number;
  closingBalance: number;
  emi: number;
  paymentStatus: string;
  paidDate?: string | null;
  cancellationReason?: string | null;
}
