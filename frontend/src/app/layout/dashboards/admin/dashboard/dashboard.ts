import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../../shared/components/admin/navbar/navbar';
import { ReportService } from '../../../features/reports/services/report.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, Navbar],
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  applications: any = {};
  cibilReport: any = {};
  scoreCardReport: any = {};
  customers: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    let completed = 0;
    const finish = () => {
      completed++;
      if (completed === 4) this.loading = false;
    };

    this.reportService.getTotalApplications().subscribe({
      next: (response: any) => this.applications = response?.data ?? response ?? {},
      error: () => { this.errorMessage = 'Some report data could not be loaded.'; finish(); },
      complete: finish
    });
    this.reportService.getCibilReport().subscribe({
      next: (response: any) => this.cibilReport = response?.data ?? response ?? {},
      error: () => { this.errorMessage = 'Some report data could not be loaded.'; finish(); },
      complete: finish
    });
    this.reportService.getScoreCardReport().subscribe({
      next: (response: any) => this.scoreCardReport = response?.data ?? response ?? {},
      error: () => { this.errorMessage = 'Some report data could not be loaded.'; finish(); },
      complete: finish
    });
    this.reportService.getCustomers().subscribe({
      next: (response: any) => {
        const data = response?.data ?? response;
        this.customers = Array.isArray(data) ? data : [];
      },
      error: () => { this.errorMessage = 'Customer list could not be loaded.'; finish(); },
      complete: finish
    });
  }

  get cibilCount(): number {
    if (Array.isArray(this.cibilReport)) return this.cibilReport.length;
    return Number(this.cibilReport?.totalCibil ?? this.cibilReport?.total ?? 0);
  }

  get scoreCardCount(): number {
    if (Array.isArray(this.scoreCardReport)) return this.scoreCardReport.length;
    return Number(this.scoreCardReport?.totalScoreCard ?? this.scoreCardReport?.total ?? 0);
  }
}
