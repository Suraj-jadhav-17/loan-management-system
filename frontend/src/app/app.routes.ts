
import { Routes } from '@angular/router';

import { Login } from './shared/components/auth/login/login';
import { MfaVerifyComponent } from './shared/components/auth/mfa-verify/mfa-verify';
import { MfaSetupComponent } from './shared/components/auth/mfa-setup/mfa-setup';
import { MfaRecoveryComponent } from './shared/components/auth/mfa-recovery/mfa-recovery';

import { Dashboard as CustomerLayout } from './layout/customer/dashboard/dashboard';
import { Dashboard as CustomerDashboard } from './layout/dashboards/customer/dashboard/dashboard';

import { LoanDeal } from './features/loan-deal/pages/loan-deal/loan-deal';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'mfa',
    component: MfaVerifyComponent
  },
  {
    path: 'mfa-setup',
    component: MfaSetupComponent
  },
  {
    path: 'mfa-recovery',
    component: MfaRecoveryComponent
  },
  {
    path: 'customer',
    component: CustomerLayout,
    children: [
      {
        path: 'dashboard',
        component: CustomerDashboard
      },
      {
        path: 'loan-deal',
        component: LoanDeal
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
