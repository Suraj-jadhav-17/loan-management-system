
import { Routes } from '@angular/router';

import {
  Dashboard as CustomerLayout
} from './layout/customer/dashboard/dashboard';
import { EmiSchedulerComponent } from './features/emi-scheduler/pages/emi-scheduler/emi-scheduler';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'customer/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'customer',
    component: CustomerLayout,
    children: [
      {
        path: 'dashboard',
        component: CustomerLayout
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'emi-scheduler',
    loadComponent: () =>
      import('./features/emi-scheduler/pages/emi-scheduler/emi-scheduler')
        .then(m => m.EmiSchedulerComponent)
  },
  {
    path: 'emi-payments',
    loadComponent: () =>
      import('./features/emi-payments/pages/emi-payments/emi-payments')
        .then(m => m.EmiPayments)
  },
  {
    path: 'my-penalties',
    loadComponent: () =>
      import('./features/penalties/pages/my-penalties/my-penalties')
        .then(m => m.MyPenalties)
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./features/notifications/pages/notifications/notifications')
        .then(m => m.Notifications)
  },
  {
    path: '**',
    redirectTo: 'customer/dashboard'
  }
  ,{
    path: 'customer/dashboard/emi-scheduler',component:EmiSchedulerComponent
  }
];
