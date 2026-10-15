
export interface EmiPayment {
  paymentId?: number;
  loanAccountId: number;
  emiScheduleId: number;
  emiAmount: number;
  penaltyAmount: number;
  totalAmount: number;
  dueDate: string;
  status: string;
}
