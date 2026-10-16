
import { Routes } from '@angular/router';
import { Dashboard as CustomerLayout } from './layout/customer/dashboard/dashboard';
import { EmiPayments } from './features/emi-payments/pages/emi-payments/emi-payments';
import { Dashboard as AdminDashboard } from './layout/admin/dashboard/dashboard';
import { LoanDeal } from './features/loan-deal/pages/loan-deal/loan-deal';
export const routes: Routes = [
  {
    path: 'user',
    component: CustomerLayout,
    children: [
      {path: 'dashboard',redirectTo: 'dashboard/home',pathMatch: 'full'},
      {path: 'dashboard/home',component: EmiPayments},
      { path: 'emi-schedule', component: EmiPayments},
      {path:'loan-deal',component:LoanDeal}
    ]
  },
  {path: '',redirectTo: 'user/dashboard',pathMatch: 'full'}
];
