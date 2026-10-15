
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
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

  score: ScoreCard | null = null;
  loading = false;
  error = '';
  message = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.cibil.getLatestScoreCard().subscribe({
      next: result => {
        this.score = result;
        this.loading = false;
      },
      error: err => {
        this.score = null;
        this.loading = false;
        this.error = err?.error?.message || 'Unable to load scorecard.';
      }
    });
  }

  generate(): void {
    this.loading = true;
    this.error = '';
    this.message = '';

    this.cibil.createScoreCard().subscribe({
      next: result => {
        this.score = result;
        this.loading = false;
        this.message = 'Scorecard created successfully.';
      },
      error: err => {
        this.loading = false;
        this.error = err?.error?.message || 'Unable to create scorecard.';
      }
    });
  }
}
