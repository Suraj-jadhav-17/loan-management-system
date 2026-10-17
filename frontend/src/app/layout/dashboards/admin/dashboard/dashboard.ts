import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { ApiResponse } from '../../../../shared/components/auth/register/models/api-response';
import { TotalApplicationsDto } from '../../../../features/reports/models/total-applications.dto';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = 'http://localhost:8080/auth/v1/loan-officer';

  constructor(private readonly http: HttpClient) {}

  getReports(): Observable<{ applications: ApiResponse<TotalApplicationsDto>; cibil: unknown; scoreCards: ApiResponse<unknown> }> {
    return forkJoin({
      applications: this.http.get<ApiResponse<TotalApplicationsDto>>(`${this.baseUrl}/totalApplication`),
      cibil: this.http.get<unknown>(`${this.baseUrl}/cibilReport`),
      scoreCards: this.http.get<ApiResponse<unknown>>(`${this.baseUrl}/scoreCardReport`)
    });
  }
}