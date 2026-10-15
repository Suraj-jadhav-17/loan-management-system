import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Dashboard } from './layout/admin/dashboard/dashboard';
import {Dashboard as CustomerDashboard } from './layout/customer/dashboard/dashboard';

@Component({
  imports: [RouterOutlet, Dashboard, CustomerDashboard],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('loan-frontend');
}
