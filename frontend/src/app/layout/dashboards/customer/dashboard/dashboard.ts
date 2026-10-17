import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../customer.service';


@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule],
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  dashboard: any = {};
  loading = true;
  errorMessage = '';

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.customerService.getDashboard().subscribe({
      next: (response: any) => {
        this.dashboard = response?.data ?? response ?? {};
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = error?.error?.message || 'Dashboard data could not be loaded.';
        this.loading = false;
      }
    });
  }
}
