
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Cibil } from '../../services/cibil';
import { ScoreCard, PageResponse } from '../../models/cibil.model';

@Component({
  selector: 'app-score-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './score-history.html',
  styleUrl: './score-history.scss'
})
export class ScoreHistory implements OnInit {
  private readonly cibil = inject(Cibil);

  data: PageResponse<ScoreCard> | null = null;
  page = 0;
  size = 10;
  loading = false;
  error = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.cibil.getScoreCardHistory(this.page, this.size).subscribe({
      next: response => {
        this.data = response;
        this.loading = false;
      },
      error: err => {
        this.error = err?.error?.message || 'Unable to load score history.';
        this.loading = false;
      }
    });
  }

  previous(): void {
    if (this.page > 0) {
      this.page--;
      this.load();
    }
  }

  next(): void {
    if (this.data && !this.data.last) {
      this.page++;
      this.load();
    }
  }
}
