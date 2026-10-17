
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Foreclosure } from '../../services/foreclosure';
import { ForeClosureResponse } from '../../models/foreclosure.model';

@Component({
  selector: 'app-foreclosure-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './foreclosure-list.html',
  styleUrl: './foreclosure-list.scss'
})
export class ForeclosureList implements OnInit {
  private readonly foreclosure = inject(Foreclosure);

  requests: ForeClosureResponse[] = [];
  loading = false;
  error = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.foreclosure.getPendingRequests().subscribe({
      next: result => {
        this.requests = Array.isArray(result) ? result : [];
        this.loading = false;
      },
      error: err => {
        this.requests = [];
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to load pending foreclosure requests.';
      }
    });
  }
}
