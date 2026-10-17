import { Component, OnInit } from '@angular/core';
import { DatePipe, JsonPipe, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReportService } from '../../services/report.service';
import { TotalApplicationsDto } from '../../models/total-applications.dto';

@Component({ selector: 'app-reports', standalone: true, imports: [NgIf, DatePipe, JsonPipe, RouterLink], templateUrl: './reports.html', styleUrl: './reports.scss' })
export class Reports implements OnInit {
  loading = true; error = ''; loadedAt = new Date(); applications: TotalApplicationsDto | null = null; cibil: unknown; scoreCards: unknown;
  constructor(private readonly reports: ReportService) {}
  ngOnInit(): void { this.refresh(); }
  refresh(): void {
    this.loading = true; this.error = '';
    this.reports.getReports().subscribe({
      next: data => { this.applications = data.applications?.data ?? null; this.cibil = (data.cibil as any)?.data ?? data.cibil; this.scoreCards = data.scoreCards?.data ?? data.scoreCards; this.loadedAt = new Date(); this.loading = false; },
      error: err => { this.loading = false; this.error = err?.status === 403 ? 'Your account does not have LoanOfficer access. These report APIs are protected by the backend role.' : (err?.error?.message || 'Could not load reports. Confirm that the backend is running and you are logged in.'); }
    });
  }
}
