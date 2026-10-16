
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

import {
  ScoreCard,
  EligibilityResult,
  PageResponse
} from '../models/cibil.model';

@Injectable({ providedIn: 'root' })
export class Cibil {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  // Customer scorecard APIs
  createScoreCard(): Observable<ScoreCard> {
    return this.http.post<ScoreCard>(
      `${this.baseUrl}/auth/v1/customer/scorecards`,
      {}
    );
  }

  getLatestScoreCard(): Observable<ScoreCard> {
    return this.http.get<ScoreCard>(
      `${this.baseUrl}/auth/v1/customer/scorecards/latest`
    );
  }

  getScoreCardHistory(
    page = 0,
    size = 10
  ): Observable<PageResponse<ScoreCard>> {
    return this.http.get<PageResponse<ScoreCard>>(
      `${this.baseUrl}/auth/v1/customer/scorecards/history`,
      { params: { page, size } }
    );
  }

  // Customer eligibility APIs
  checkEligibility(): Observable<EligibilityResult> {
    return this.http.post<EligibilityResult>(
      `${this.baseUrl}/auth/v1/customer/eligibility/check`,
      {}
    );
  }

  getLatestEligibility(): Observable<EligibilityResult> {
    return this.http.get<EligibilityResult>(
      `${this.baseUrl}/auth/v1/customer/eligibility/latest`
    );
  }

  getEligibilityHistory(
    page = 0,
    size = 10
  ): Observable<PageResponse<EligibilityResult>> {
    return this.http.get<PageResponse<EligibilityResult>>(
      `${this.baseUrl}/auth/v1/customer/eligibility/history`,
      { params: { page, size } }
    );
  }

  // Loan Officer scorecard APIs
  createCustomerScoreCard(customerId: number): Observable<ScoreCard> {
    return this.http.post<ScoreCard>(
      `${this.baseUrl}/auth/v1/loan-office/scorecards/${customerId}`,
      {}
    );
  }

  getCustomerScoreCard(customerId: number): Observable<ScoreCard> {
    return this.http.get<ScoreCard>(
      `${this.baseUrl}/auth/v1/loan-office/scorecards/${customerId}/latest`
    );
  }

  getCustomerScoreCardHistory(
    customerId: number,
    page = 0,
    size = 10
  ): Observable<PageResponse<ScoreCard>> {
    return this.http.get<PageResponse<ScoreCard>>(
      `${this.baseUrl}/auth/v1/loan-office/scorecards/${customerId}/history`,
      { params: { page, size } }
    );
  }

  // Loan Officer eligibility APIs
  checkCustomerEligibility(
    customerId: number
  ): Observable<EligibilityResult> {
    return this.http.post<EligibilityResult>(
      `${this.baseUrl}/auth/v1/loan-office/eligibility/${customerId}/check`,
      {}
    );
  }

  getCustomerEligibility(
    customerId: number
  ): Observable<EligibilityResult> {
    return this.http.get<EligibilityResult>(
      `${this.baseUrl}/auth/v1/loan-office/eligibility/${customerId}/latest`
    );
  }

  getCustomerEligibilityHistory(
    customerId: number,
    page = 0,
    size = 10
  ): Observable<PageResponse<EligibilityResult>> {
    return this.http.get<PageResponse<EligibilityResult>>(
      `${this.baseUrl}/auth/v1/loan-office/eligibility/${customerId}/history`,
      { params: { page, size } }
    );
  }

  reviewCustomerEligibility(
    customerId: number,
    decision: 'APPROVE' | 'REJECT'
  ): Observable<EligibilityResult> {
    return this.http.put<EligibilityResult>(
      `${this.baseUrl}/auth/v1/loan-office/eligibility/${customerId}/review`,
      null,
      { params: { decision } }
    );
  }
}
