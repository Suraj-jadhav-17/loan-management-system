
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../../../shared/components/admin/navbar/navbar';
import { ReportService } from '../../../../features/reports/services/report.service';

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
    this.loading = true;
    this.errorMessage = '';

    this.reportService.getReports().subscribe({
      next: (response: any) => {
        this.applications = response?.applications?.data
          ?? response?.applications
          ?? {};

        this.cibilReport = response?.cibil?.data
          ?? response?.cibil
          ?? {};

        this.scoreCardReport = response?.scoreCards?.data
          ?? response?.scoreCards
          ?? {};

        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Dashboard reports could not be loaded.';
        this.loading = false;
      }
    });
  }

  get cibilCount(): number {
    if (Array.isArray(this.cibilReport)) {
      return this.cibilReport.length;
    }

    return Number(
      this.cibilReport?.totalCibil ??
      this.cibilReport?.total ??
      0
    );
  }

  get scoreCardCount(): number {
    if (Array.isArray(this.scoreCardReport)) {
      return this.scoreCardReport.length;
    }

    return Number(
      this.scoreCardReport?.totalScoreCard ??
      this.scoreCardReport?.total ??
      0
    );
  }
}
