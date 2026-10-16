export interface RegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
  age: number;
  password: string;
  mobileNo: string;
  panCard: string;
  aadhaarNo: string;
  employmentType: string;
  monthlyIncome: number;
  monthlyInvestment: number | null;
}
