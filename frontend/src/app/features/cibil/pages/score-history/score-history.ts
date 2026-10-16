
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Cibil } from '../../services/cibil';
import { PageResponse, ScoreCard } from '../../models/cibil.model';

@Component({
  selector: 'app-score-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './score-history.html',
  styleUrl: './score-history.scss'
})
export class ScoreHistory implements OnInit {
  private readonly cibil = inject(Cibil);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  data: PageResponse<ScoreCard> | null = null;
  customerId: number | null = null;
  officerMode = false;
  page = 0;
  size = 10;
  loading = false;
  error = '';

  ngOnInit(): void {
    this.officerMode = this.router.url.startsWith('/loan-officer/');
    const id = Number(this.route.snapshot.queryParamMap.get('customerId'));

    if (this.officerMode) {
      if (Number.isInteger(id) && id > 0) {
        this.customerId = id;
        this.load();
      } else {
        this.error = 'Enter a valid customer ID to load score history.';
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

    const request = this.officerMode
      ? this.cibil.getCustomerScoreCardHistory(
          this.customerId!, this.page, this.size
        )
      : this.cibil.getScoreCardHistory(this.page, this.size);

    request.subscribe({
      next: response => {
        this.data = response;
        this.loading = false;
      },
      error: err => {
        this.error =
          err?.error?.message || 'Unable to load scorecard history.';
        this.loading = false;
      }
    });
  }

  previous(): void {
    if (this.page <= 0 || this.loading) return;
    this.page--;
    this.load();
  }

  next(): void {
    if (!this.data || this.data.last || this.loading) return;
    this.page++;
    this.load();
  }

  setCustomerId(value: string): void {
    const id = Number(value);
    this.customerId = Number.isInteger(id) && id > 0 ? id : null;
  }

  searchCustomer(): void {
    if (!this.customerId) {
      this.error = 'Enter a valid customer ID.';
      return;
    }

    this.page = 0;
    this.data = null;
    this.load();
  }
}
