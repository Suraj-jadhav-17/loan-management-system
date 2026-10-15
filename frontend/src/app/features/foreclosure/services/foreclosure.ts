
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

import {
  ForeClosureCreateRequest,
  ForeClosureResponse,
  ForeClosurePayment
} from '../models/foreclosure.model';

@Injectable({ providedIn: 'root' })
export class Foreclosure {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  // Customer APIs
  createRequest(
    request: ForeClosureCreateRequest
  ): Observable<ForeClosureResponse> {
    return this.http.post<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/customer/foreclosure/requests`,
      request
    );
  }

  getCustomerRequest(id: number): Observable<ForeClosureResponse> {
    return this.http.get<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/customer/foreclosure/requests/${id}`
    );
  }

  createPaymentOrder(id: number): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/auth/v1/customer/foreclosure/requests/${id}/payment-order`,
      {},
      { responseType: 'text' }
    );
  }

  makePayment(
    payment: ForeClosurePayment
  ): Observable<ForeClosureResponse> {
    return this.http.post<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/customer/foreclosure/payments`,
      payment
    );
  }

  // Loan Officer APIs
  getPendingRequests(): Observable<ForeClosureResponse[]> {
    return this.http.get<ForeClosureResponse[]>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/requests/pending`
    );
  }

  getOfficerRequest(id: number): Observable<ForeClosureResponse> {
    return this.http.get<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/requests/${id}`
    );
  }

  approveRequest(
    id: number,
    remarks = ''
  ): Observable<ForeClosureResponse> {
    return this.http.post<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/requests/${id}/approve`,
      { foreClosureId: id, remarks }
    );
  }

  rejectRequest(
    id: number,
    remarks = ''
  ): Observable<ForeClosureResponse> {
    return this.http.post<ForeClosureResponse>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/requests/${id}/reject`,
      { foreClosureId: id, remarks }
    );
  }
}
