
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private readonly baseUrl = 'http://localhost:8080/auth/v1/customer';

  constructor(private readonly http: HttpClient) {}

  private getOptions() {
    const token =
      sessionStorage.getItem('accessToken') ||
      localStorage.getItem('accessToken');

    return {
      headers: new HttpHeaders(
        token
          ? { Authorization: `Bearer ${token}` }
          : {}
      )
    };
  }

  getDashboard(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/dashboard`,
      this.getOptions()
    );
  }

  getProfile(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/profile`,
      this.getOptions()
    );
  }

  getEmiSummary(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/emi-summary`,
      this.getOptions()
    );
  }

  getReceiptsPayments(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/reciptsPayments`,
      this.getOptions()
    );
  }
}