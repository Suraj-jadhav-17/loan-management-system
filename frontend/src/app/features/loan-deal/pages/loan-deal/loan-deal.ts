import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  imports: [FormsModule],
  selector: 'app-loan-deal',
  styleUrl: './loan-deal.scss',
  templateUrl: './loan-deal.html',
})
export class LoanDeal {

  loanTypes= [
    {name:'Home Loan', value:'HOME_LOAN'},
    {name:'Car Loan', value:'CAR_LOAN'}
  ]

  loanDeal = {
    loanType: '',
    amount:null as number | null,
    tenureMonths:null as number | null,
    emiAmount:null as number | null,
    bankName:'',
    bankAccountNumber:'',
    ifscCode:'',
    emiDay:null as number | null
  }
}
