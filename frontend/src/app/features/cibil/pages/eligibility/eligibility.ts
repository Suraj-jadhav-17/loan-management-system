
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  result: EligibilityResult | null = null;
  customerId: number | null = null;
  officerMode = false;

  loading = false;
  checking = false;
  reviewing = false;

  error = '';
  message = '';

  ngOnInit(): void {
    this.officerMode = this.router.url.startsWith('/loan-officer/');

    const id = Number(
      this.route.snapshot.queryParamMap.get('customerId')
    );

    if (this.officerMode) {
      if (Number.isInteger(id) && id > 0) {
        this.customerId = id;
        this.loadLatest();
      } else {
        this.error = 'Enter a valid customer ID to load eligibility.';
      }
      return;
    }

    this.loadLatest();
  }

  loadLatest(): void {
    if (this.loading || this.checking || this.reviewing) {
      return;
    }

    if (this.officerMode && (!this.customerId || this.customerId <= 0)) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    const request = this.officerMode
      ? this.cibil.getCustomerEligibility(this.customerId!)
      : this.cibil.getLatestEligibility();

    request.subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
      },
      error: err => {
        this.result = null;
        this.loading = false;
        this.error =
          err?.error?.message ||
          'Unable to load the eligibility result.';
      }
    });
  }

  check(): void {
    if (this.checking || this.loading || this.reviewing) {
      return;
    }

    if (this.officerMode && (!this.customerId || this.customerId <= 0)) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    this.checking = true;
    this.error = '';
    this.message = '';

    const request = this.officerMode
      ? this.cibil.checkCustomerEligibility(this.customerId!)
      : this.cibil.checkEligibility();

    request.subscribe({
      next: data => {
        this.result = data;
        this.checking = false;
        this.message = 'Eligibility check completed successfully.';
      },
      error: err => {
        this.checking = false;
        this.error =
          err?.error?.message || 'Unable to check loan eligibility.';
      }
    });
  }

  review(decision: 'APPROVE' | 'REJECT'): void {
    if (this.reviewing || this.loading || this.checking) {
      return;
    }

    if (!this.officerMode) {
      this.error = 'Only a loan officer can review eligibility.';
      return;
    }

    if (!this.customerId || this.customerId <= 0) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    if (!this.result) {
      this.error = 'Load an eligibility result before reviewing it.';
      return;
    }

    this.reviewing = true;
    this.error = '';
    this.message = '';

    this.cibil.reviewCustomerEligibility(
      this.customerId,
      decision
    ).subscribe({
      next: data => {
        this.result = data;
        this.reviewing = false;
        this.message =
          decision === 'APPROVE'
            ? 'Eligibility approved successfully.'
            : 'Eligibility rejected successfully.';
      },
      error: err => {
        this.reviewing = false;
        this.error =
          err?.error?.message ||
          `Unable to ${decision.toLowerCase()} eligibility.`;
      }
    });
  }

  setCustomerId(value: string): void {
    const id = Number(value);

    this.customerId =
      Number.isInteger(id) && id > 0 ? id : null;

    this.result = null;
    this.error = '';
    this.message = '';
  }
}
