
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Cibil } from '../../services/cibil';
import { ScoreCard } from '../../models/cibil.model';

@Component({
  selector: 'app-scorecard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './scorecard.html',
  styleUrl: './scorecard.scss'
})
export class Scorecard implements OnInit {
  private readonly cibil = inject(Cibil);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  score: ScoreCard | null = null;
  customerId: number | null = null;
  officerMode = false;
  loading = false;
  error = '';
  message = '';

  ngOnInit(): void {
    this.officerMode = this.router.url.startsWith('/loan-officer/');
    const id = Number(this.route.snapshot.queryParamMap.get('customerId'));

    if (this.officerMode) {
      if (Number.isInteger(id) && id > 0) {
        this.customerId = id;
        this.load();
      } else {
        this.error = 'Enter a valid customer ID to load the scorecard.';
      }
      return;
    }

    this.load();
  }

  load(): void {
    if (this.loading) return;

    if (this.officerMode && (!this.customerId || this.customerId <= 0)) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    const request = this.officerMode
      ? this.cibil.getCustomerScoreCard(this.customerId!)
      : this.cibil.getLatestScoreCard();

    request.subscribe({
      next: result => {
        this.score = result;
        this.loading = false;
        if (!result) this.error = 'No scorecard found.';
      },
      error: err => {
        this.score = null;
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to load the scorecard.';
      }
    });
  }

  generate(): void {
    if (this.loading) return;

    if (this.officerMode && (!this.customerId || this.customerId <= 0)) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    const request = this.officerMode
      ? this.cibil.createCustomerScoreCard(this.customerId!)
      : this.cibil.createScoreCard();

    request.subscribe({
      next: result => {
        this.score = result;
        this.loading = false;
        this.message = 'Scorecard generated successfully.';
      },
      error: err => {
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to generate scorecard.';
      }
    });
  }

  setCustomerId(value: string): void {
    const id = Number(value);
    this.customerId = Number.isInteger(id) && id > 0 ? id : null;
  }
}
