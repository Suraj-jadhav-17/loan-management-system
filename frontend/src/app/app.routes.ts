import { Routes } from '@angular/router';
import { Dashboard as CustomerLayout } from './layout/customer/dashboard/dashboard';
export const routes: Routes = [{
    path: 'customer',
    component: CustomerLayout,
    children: [{ path: 'dashboard',component: CustomerLayout},
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: 'customer/dashboard',
    pathMatch: 'full'
  }];
