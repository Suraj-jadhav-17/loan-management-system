
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class CustomerDashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getDashboard(): Observable<any> {
    const token =
      typeof sessionStorage !== 'undefined'
        ? sessionStorage.getItem('accessToken')
        : null;

    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http.get<any>(
      `${this.baseUrl}/auth/v1/customer/dashboard`,
      { headers }
    );
  }
}
