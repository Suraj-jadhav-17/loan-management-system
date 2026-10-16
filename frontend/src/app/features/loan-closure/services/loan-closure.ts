
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoanClosure } from '../models/loan-closure.model';

@Injectable({
  providedIn: 'root'
})
export class LoanClosureService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Customer: get closure details for a loan account.
   */
  getCustomerClosure(loanAccountId: number): Observable<LoanClosure> {
    return this.http.get<LoanClosure>(
      `${this.baseUrl}/auth/v1/customer/loan-closure/${loanAccountId}`
    );
  }

  /**
   * Loan officer: get closure details for a loan account.
   */
  getOfficerClosure(loanAccountId: number): Observable<LoanClosure> {
    return this.http.get<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/closures/${loanAccountId}`
    );
  }

  /**
   * Loan officer: close a loan normally.
   */
  closeNormally(loanAccountId: number): Observable<LoanClosure> {
    return this.http.post<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/foreclosure/closures/${loanAccountId}/normal`,
      {}
    );
  }

  /**
   * Loan officer: close a loan through foreclosure.
   */
  closeByForeclosure(loanAccountId: number): Observable<LoanClosure> {
    return this.http.post<LoanClosure>(
      `${this.baseUrl}/auth/v1/loan-office/loan-closure/${loanAccountId}/foreclosure`,
      {}
    );
  }
}
