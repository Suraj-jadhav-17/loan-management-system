
import { Routes } from '@angular/router';

import { Login } from './shared/components/auth/login/login';
import { Register } from './shared/components/auth/register/register';

import { Dashboard as CustomerDashboard } from './layout/customer/dashboard/dashboard';
import { Dashboard as AdminDashboard } from './layout/admin/dashboard/dashboard';

import { Scorecard } from './features/cibil/pages/scorecard/scorecard';
import { ScoreHistory } from './features/cibil/pages/score-history/score-history';
import { Eligibility } from './features/cibil/pages/eligibility/eligibility';

import { ForeclosureRequestPage } from './features/foreclosure/pages/foreclosure-request/foreclosure-request';
import { ForeclosureList } from './features/foreclosure/pages/foreclosure-list/foreclosure-list';
import { ForeclosureDetail } from './features/foreclosure/pages/foreclosure-detail/foreclosure-detail';

import { ClosureDetailsPage } from './features/loan-closure/pages/closure-details/closure-details';
import { ClosureHistory } from './features/loan-closure/pages/closure-history/closure-history';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },

  // CUSTOMER LAYOUT
  {
    path: 'customer',
    component: CustomerDashboard,
    children: [
      {
        path: '',
        redirectTo: 'cibil/scorecard',
        pathMatch: 'full'
      },
      {
        path: 'cibil/scorecard',
        component: Scorecard
      },
      {
        path: 'cibil/history',
        component: ScoreHistory
      },
      {
        path: 'cibil/eligibility',
        component: Eligibility
      },
      {
        path: 'foreclosure/request',
        component: ForeclosureRequestPage
      },
      {
        path: 'loan-closure',
        component: ClosureDetailsPage
      },
      {
        path: 'loan-closure/history',
        component: ClosureHistory
      }
    ]
  },

  // LOAN OFFICER / ADMIN LAYOUT
  {
    path: 'loan-officer',
    component: AdminDashboard,
    children: [
      {
        path: '',
        redirectTo: 'foreclosure',
        pathMatch: 'full'
      },
      {
        path: 'foreclosure',
        component: ForeclosureList
      },
      {
        path: 'foreclosure/detail/:id',
        component: ForeclosureDetail
      },
      {
        path: 'loan-closure',
        component: ClosureDetailsPage
      },
      {
        path: 'loan-closure/history',
        component: ClosureHistory
      },
      {
        path: 'cibil/scorecard',
        component: Scorecard
      },
      {
        path: 'cibil/history',
        component: ScoreHistory
      },
      {
        path: 'cibil/eligibility',
        component: Eligibility
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
