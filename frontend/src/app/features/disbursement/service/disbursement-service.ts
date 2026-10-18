import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import { ApiResponse, Disbursement, DisbursementRequest, PageResponse } from '../models/disbursement.model';

@Service()
export class DisbursementService {
     private readonly http = inject(HttpClient);

  private readonly disbursementUrl =
    `${environment.apiUrl}/v1/disbursement`;

  private readonly sanctionLetterUrl =
    `${environment.apiUrl}/v1/sanction-letter`;

  getAllDisbursements(
    page = 0,
    size = 10
  ): Observable<ApiResponse<PageResponse<Disbursement>>> {
    return this.http.get<ApiResponse<PageResponse<Disbursement>>>(
      this.disbursementUrl,
      {
        params: { page, size }
      }
    );
  }

  completeDisbursement(
    request: DisbursementRequest
  ): Observable<ApiResponse<Disbursement>> {
    return this.http.put<ApiResponse<Disbursement>>(
      `${this.disbursementUrl}/complete`,
      request
    );
  }

  viewSanctionLetter(loanDealId: number): Observable<Blob> {
    return this.http.get(
      `${this.sanctionLetterUrl}/deal/${loanDealId}/view`,
      {
        responseType: 'blob'
      }
    );
  }

  downloadSanctionLetter(loanDealId: number): Observable<Blob> {
    return this.http.get(
      `${this.sanctionLetterUrl}/deal/${loanDealId}/download`,
      {
        responseType: 'blob'
      }
    );
  }

  getSanctionLetterById(id: number): Observable<ApiResponse<unknown>> {
  return this.http.get<ApiResponse<unknown>>(
    `${this.sanctionLetterUrl}/${id}`
  );
}
}
