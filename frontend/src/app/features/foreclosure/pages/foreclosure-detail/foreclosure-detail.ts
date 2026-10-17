
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Foreclosure } from '../../services/foreclosure';
import { ForeClosureResponse } from '../../models/foreclosure.model';

@Component({
  selector: 'app-foreclosure-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './foreclosure-detail.html',
  styleUrl: './foreclosure-detail.scss'
})
export class ForeclosureDetail implements OnInit {
  private readonly foreclosure = inject(Foreclosure);
  private readonly route = inject(ActivatedRoute);

  request: ForeClosureResponse | null = null;

  remarks = '';
  loading = false;
  saving = false;
  error = '';
  message = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.error = 'Invalid foreclosure request ID.';
      return;
    }

    this.load(id);
  }

  load(id: number): void {
    if (this.loading || this.saving) {
      return;
    }

    if (!Number.isInteger(id) || id <= 0) {
      this.error = 'Invalid foreclosure request ID.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    this.foreclosure.getOfficerRequest(id).subscribe({
      next: data => {
        this.request = data;
        this.remarks = data.remarks ?? '';
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        this.error =
          err?.error?.message ||
          'Unable to load foreclosure request details.';
      }
    });
  }

  approve(): void {
    if (
      !this.request ||
      this.request.status !== 'PENDING' ||
      this.saving ||
      this.loading
    ) {
      return;
    }

    this.saving = true;
    this.error = '';
    this.message = '';

    this.foreclosure
      .approveRequest(this.request.foreClosureId, this.remarks)
      .subscribe({
        next: data => {
          this.request = data;
          this.remarks = data.remarks ?? this.remarks;
          this.saving = false;
          this.message = 'Foreclosure request approved.';
        },
        error: err => {
          this.saving = false;
          this.error =
            err?.error?.message || 'Unable to approve request.';
        }
      });
  }

  reject(): void {
    if (
      !this.request ||
      this.request.status !== 'PENDING' ||
      this.saving ||
      this.loading
    ) {
      return;
    }

    this.saving = true;
    this.error = '';
    this.message = '';

    this.foreclosure
      .rejectRequest(this.request.foreClosureId, this.remarks)
      .subscribe({
        next: data => {
          this.request = data;
          this.remarks = data.remarks ?? this.remarks;
          this.saving = false;
          this.message = 'Foreclosure request rejected.';
        },
        error: err => {
          this.saving = false;
          this.error =
            err?.error?.message || 'Unable to reject request.';
        }
      });
  }
}
