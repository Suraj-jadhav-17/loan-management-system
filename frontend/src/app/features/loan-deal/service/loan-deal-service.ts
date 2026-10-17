
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoanDealRequest } from '../model/loan-deal-request.model';
import { ApiResponse, LoanDealResponse } from '../model/oan-deal-response.model';


@Injectable({
  providedIn: 'root'
})
export class LoanDealService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/v1/loandeal`;

  createLoanDeal(
    request: LoanDealRequest
  ): Observable<ApiResponse<LoanDealResponse>> {
    return this.http.post<ApiResponse<LoanDealResponse>>(
      this.apiUrl,
      request
    );
  }

  getLoanDealById(
    id: number
  ): Observable<ApiResponse<LoanDealResponse>> {
    return this.http.get<ApiResponse<LoanDealResponse>>(
      `${this.apiUrl}/${id}`
    );
  }

  getLoanDealsByCustomerId(
    customerId: number
  ): Observable<ApiResponse<LoanDealResponse[]>> {
    return this.http.get<ApiResponse<LoanDealResponse[]>>(
      `${this.apiUrl}/customer/${customerId}`
    );
  }

  getLoanDealsByLoanType(
    loanType: 'HOME_LOAN' | 'CAR_LOAN'
  ): Observable<ApiResponse<LoanDealResponse[]>> {
    return this.http.get<ApiResponse<LoanDealResponse[]>>(
      `${this.apiUrl}/type/${loanType}`
    );
  }
}
