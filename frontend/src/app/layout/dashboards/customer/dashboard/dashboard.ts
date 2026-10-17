
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../../../shared/components/customer/navbar/navbar';
import { Topbar } from '../../../../shared/components/customer/topbar/topbar';
import { CustomerDashboardService } from '../services/customer-dashboard.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, Navbar, Topbar],
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  dashboard: any = {};
  loading = true;
  errorMessage = '';

  constructor(
    private customerService: CustomerDashboardService
  ) {}

  ngOnInit(): void {
    this.customerService.getDashboard().subscribe({
      next: (response: any) => {
        this.dashboard = response?.data ?? response ?? {};
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage =
          error?.error?.message ||
          'Dashboard data could not be loaded.';
        this.loading = false;
      }
    });
  }
}
