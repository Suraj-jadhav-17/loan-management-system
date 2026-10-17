import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {  Dashboard as CustomerLayout} from './layout/customer/dashboard/dashboard';
import { LoanDealReview } from './features/loan-deal-review/pages/loan-deal-review/loan-deal-review';

@Component({
  imports: [RouterOutlet, CustomerLayout,LoanDealReview],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('loan-frontend');
}
