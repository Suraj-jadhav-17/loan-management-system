import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';

import { TotalApplicationsDto } from '../models/total-applications.dto';
import { ApiResponse } from '../../../shared/components/auth/register/models/api-response';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = 'http://localhost:8080/auth/v1/loan-officer';
  constructor(private readonly http: HttpClient) {}

  private options() {
    const token = typeof sessionStorage !== 'undefined' ? sessionStorage.getItem('accessToken') : null;
    return { headers: token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders() };
  }

  getReports(): Observable<{ applications: ApiResponse<TotalApplicationsDto>; cibil: unknown; scoreCards: ApiResponse<unknown> }> {
    const options = this.options();
    return forkJoin({
      applications: this.http.get<ApiResponse<TotalApplicationsDto>>(`${this.baseUrl}/totalApplication`, options),
      // This endpoint currently returns the map directly (not ApiResponse) in the backend controller.
      cibil: this.http.get<unknown>(`${this.baseUrl}/cibilReport`, options),
      scoreCards: this.http.get<ApiResponse<unknown>>(`${this.baseUrl}/scoreCardReport`, options)
    });
  }
}
