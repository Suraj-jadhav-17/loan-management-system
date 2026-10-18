
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse, DealReview, PageResponse } from '../model/deal-review.model';


@Injectable({
  providedIn: 'root'
})
export class DealReviewService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/v1/deal-review`;

  getReviews(
    page = 0,
    size = 10
  ): Observable<ApiResponse<PageResponse<DealReview>>> {
    return this.http.get<ApiResponse<PageResponse<DealReview>>>(
      this.apiUrl,
      { params: { page, size } }
    );
  }

  getReviewsByStatus(
    status: string,
    page = 0,
    size = 10
  ): Observable<ApiResponse<PageResponse<DealReview>>> {
    return this.http.get<ApiResponse<PageResponse<DealReview>>>(
      `${this.apiUrl}/status/${status}`,
      { params: { page, size } }
    );
  }

  updateReview(
    reviewId: number,
    request: {
      loanDealId: number;
      officerId: number;
      status: string;
    }
  ): Observable<ApiResponse<DealReview>> {
    return this.http.put<ApiResponse<DealReview>>(
      `${this.apiUrl}/${reviewId}`,
      request
    );
  }
}
