
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Cibil } from '../../services/cibil';
import { EligibilityResult } from '../../models/cibil.model';

@Component({
  selector: 'app-eligibility',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './eligibility.html',
  styleUrl: './eligibility.scss'
})
export class Eligibility implements OnInit {
  private readonly cibil = inject(Cibil);

  result: EligibilityResult | null = null;
  loading = false;
  checking = false;
  error = '';
  message = '';

  ngOnInit(): void {
    this.loadLatest();
  }

  loadLatest(): void {
    this.loading = true;
    this.error = '';

    this.cibil.getLatestEligibility().subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
      },
      error: err => {
        this.result = null;
        this.loading = false;
        this.error = err?.error?.message || 'No eligibility result found.';
      }
    });
  }

  check(): void {
    this.checking = true;
    this.error = '';
    this.message = '';

    this.cibil.checkEligibility().subscribe({
      next: data => {
        this.result = data;
        this.checking = false;
        this.message = 'Eligibility check completed.';
      },
      error: err => {
        this.checking = false;
        this.error = err?.error?.message || 'Unable to check eligibility.';
      }
    });
  }
}
