
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoanClosure } from '../models/loan-closure.model';

@Injectable({ providedIn: 'root' })
export class LoanClosureService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  // Customer: get closure details
  getCustomerClosure(id: number): Observable<LoanClosure> {
    return this.http.get<LoanClosure>(
      `${this.baseUrl}/auth/v1/customer/loan-closure/${id}`
    );
  }

  // Loan Officer: get closure details
  getOfficerClosure(id: number): Observable<LoanClosure> {
    return this.http.get<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/loan-closure/${id}`
    );
  }

  // Loan Officer: normal closure
  closeNormally(id: number): Observable<LoanClosure> {
    return this.http.post<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/loan-closure/${id}/normal`,
      {}
    );
  }

  // Loan Officer: foreclosure closure
  closeByForeclosure(id: number): Observable<LoanClosure> {
    return this.http.post<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/loan-closure/${id}/foreclosure`,
      {}
    );
  }
}
