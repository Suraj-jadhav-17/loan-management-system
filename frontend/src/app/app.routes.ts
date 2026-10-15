
import { Routes } from '@angular/router';

export const routes: Routes = [
  // CIBIL
  {
    path: 'cibil',
    loadComponent: () =>
      import('./features/cibil/pages/scorecard/scorecard')
        .then(m => m.Scorecard)
  },
  {
    path: 'cibil/eligibility',
    loadComponent: () =>
      import('./features/cibil/pages/eligibility/eligibility')
        .then(m => m.Eligibility)
  },
  {
    path: 'cibil/history',
    loadComponent: () =>
      import('./features/cibil/pages/score-history/score-history')
        .then(m => m.ScoreHistory)
  },

  // Foreclosure
  {
    path: 'foreclosure',
    loadComponent: () =>
      import('./features/foreclosure/pages/foreclosure-request/foreclosure-request')
        .then(m => m.ForeclosureRequestPage)
  },
  {
    path: 'foreclosure/list',
    loadComponent: () =>
      import('./features/foreclosure/pages/foreclosure-list/foreclosure-list')
        .then(m => m.ForeclosureList)
  },
  {
    path: 'foreclosure/detail/:id',
    loadComponent: () =>
      import('./features/foreclosure/pages/foreclosure-detail/foreclosure-detail')
        .then(m => m.ForeclosureDetail)
  },

  // Loan Closure
  {
    path: 'loan-closure',
    loadComponent: () =>
      import('./features/loan-closure/pages/closure-details/closure-details')
        .then(m => m.ClosureDetailsPage)
  },
  {
    path: 'loan-closure/history',
    loadComponent: () =>
      import('./features/loan-closure/pages/closure-history/closure-history')
        .then(m => m.ClosureHistory)
  },

  {
    path: '',
    redirectTo: 'cibil',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'cibil'
  }
];
