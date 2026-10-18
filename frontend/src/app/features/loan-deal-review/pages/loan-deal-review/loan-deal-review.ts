
import { Component, inject, OnInit } from '@angular/core';
import { DealReviewService } from '../../service/deal-review-service';
import { DealReview } from '../../model/deal-review.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-loan-deal-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrl: './loan-deal-review.scss',
  templateUrl: './loan-deal-review.html',
})
export class LoanDealReview implements OnInit {
  private readonly dealReviewService = inject(DealReviewService);

  reviews: DealReview[] = [];
  selectedReview: DealReview | null = null;

  isLoading = false;
  isUpdating = false;
  errorMessage = '';
  actionMessage = '';

  page = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  searchText = '';
  selectedLoanType = '';
  minAmount: number | null = null;
  maxAmount: number | null = null;
  startDate = '';
  endDate = '';

  readonly pageSizes = [10, 25, 50, 100];

  ngOnInit(): void {
    this.loadPendingDeals();
  }

  loadPendingDeals(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.dealReviewService
      .getReviewsByStatus('PENDING', this.page, this.pageSize)
      .subscribe({
        next: (response) => {
          this.reviews = response.data?.content ?? [];
          this.totalElements = response.data?.totalElements ?? 0;
          this.totalPages = response.data?.totalPages ?? 0;
          this.isLoading = false;
        },
        error: (error) => {
          this.reviews = [];
          this.errorMessage =
            error.error?.message || 'Unable to load deal reviews.';
          this.isLoading = false;
        }
      });
  }

  get filteredReviews(): DealReview[] {
    const search = this.searchText.trim().toLowerCase();

    return this.reviews.filter((review) => {
      const customerName = [
        review.loanDeal?.customer?.firstName,
        review.loanDeal?.customer?.lastName
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      const loanType =
        review.loanDeal?.loanType?.toString().toLowerCase() ?? '';

      const matchesSearch =
        !search ||
        customerName.includes(search) ||
        loanType.includes(search) ||
        review.status?.toLowerCase().includes(search);

      const amount = review.loanDeal?.amount ?? 0;

      const matchesMin =
        this.minAmount === null || amount >= this.minAmount;

      const matchesMax =
        this.maxAmount === null || amount <= this.maxAmount;

      const matchesType =
        !this.selectedLoanType ||
        review.loanDeal?.loanType?.toString() === this.selectedLoanType;

      const appliedDate =
        review.loanDeal?.appliedDate ||
        review.loanDeal?.createdAt ||
        '';

      const date = appliedDate.slice(0, 10);

      const matchesStart = !this.startDate || date >= this.startDate;
      const matchesEnd = !this.endDate || date <= this.endDate;

      return (
        matchesSearch &&
        matchesMin &&
        matchesMax &&
        matchesType &&
        matchesStart &&
        matchesEnd
      );
    });
  }

  get loanTypes(): string[] {
    return [
      ...new Set(
        this.reviews
          .map((review) => review.loanDeal?.loanType?.toString())
          .filter((type): type is string => Boolean(type))
      )
    ];
  }

  openReview(review: DealReview): void {
    this.selectedReview = review;
    this.actionMessage = '';
    this.errorMessage = '';
  }

  closeReview(): void {
    if (!this.isUpdating) {
      this.selectedReview = null;
    }
  }

  updateReviewStatus(status: 'APPROVED' | 'REJECTED'): void {
    if (!this.selectedReview || this.isUpdating) {
      return;
    }

    const review = this.selectedReview;

    // Temporary testing value; replace with the logged-in officer's ID.
    const officerId = 5;

    this.isUpdating = true;
    this.errorMessage = '';
    this.actionMessage = '';

    this.dealReviewService
      .updateReview(review.reviewId, {
        loanDealId: review.loanDeal.id,
        officerId,
        status
      })
      .subscribe({
        next: (response) => {
          this.actionMessage =
            response.message ||
            `Deal review ${status.toLowerCase()} successfully.`;

          this.selectedReview = null;
          this.isUpdating = false;
          this.loadPendingDeals();
        },
        error: (error) => {
          this.errorMessage =
            error.error?.message || 'Unable to update the deal review.';
          this.isUpdating = false;
        }
      });
  }

  clearFilters(): void {
    this.searchText = '';
    this.selectedLoanType = '';
    this.minAmount = null;
    this.maxAmount = null;
    this.startDate = '';
    this.endDate = '';
    this.page = 0;
    this.loadPendingDeals();
  }

  changePageSize(value: string): void {
    this.pageSize = Number(value);
    this.page = 0;
    this.loadPendingDeals();
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadPendingDeals();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadPendingDeals();
    }
  }

  get firstEntry(): number {
    return this.totalElements === 0
      ? 0
      : this.page * this.pageSize + 1;
  }

  get lastEntry(): number {
    return Math.min(
      (this.page + 1) * this.pageSize,
      this.totalElements
    );
  }
}
