import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegistrationRequest } from '../models/registration-request.model';
import { VerifyOtpRequest } from '../models/verify-otp.model';
import { LoginRequest } from '../models/login-request.model';
import { AuthFlowResponse, LoginTokens } from '../models/auth-flow-response.model';
import { ApiResponse } from '../models/api-response';

@Injectable({ providedIn: 'root' })
export class Auth {
  readonly baseUrl = 'http://localhost:8080/auth/v1';
  constructor(private readonly http: HttpClient) {}

  register(data: RegistrationRequest): Observable<ApiResponse<unknown>> {
    return this.http.post<ApiResponse<unknown>>(`${this.baseUrl}/register`, data, { withCredentials: true });
  }

  verifyOtp(data: VerifyOtpRequest): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/verified-otp`, data, { withCredentials: true, responseType: 'text' });
  }

  login(data: LoginRequest): Observable<AuthFlowResponse> {
    return this.http.post<AuthFlowResponse>(`${this.baseUrl}/login`, data, { withCredentials: true });
  }

  setupAuthenticator(token: string): Observable<AuthFlowResponse> {
    return this.http.post<AuthFlowResponse>(`${this.baseUrl}/authenticator/setup`, {}, { headers: new HttpHeaders({ 'X-Verification-Token': token }), withCredentials: true });
  }

  verifyAuthenticatorSetup(token: string, otp: string): Observable<LoginTokens> {
    return this.http.post<LoginTokens>(`${this.baseUrl}/authenticator/verify-setup`, { otp }, { headers: new HttpHeaders({ 'X-Verification-Token': token }), withCredentials: true });
  }

  verifyAuthenticatorLogin(token: string, otp: string): Observable<LoginTokens> {
    return this.http.post<LoginTokens>(`${this.baseUrl}/authenticator/verify-login`, { otp }, { headers: new HttpHeaders({ 'X-Verification-Token': token }), withCredentials: true });
  }

  requestRecoveryOtp(token: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/recovery/request`, {}, { headers: new HttpHeaders({ 'X-Verification-Token': token }), withCredentials: true });
  }

  verifyRecoveryOtp(token: string, otp: string): Observable<AuthFlowResponse> {
    return this.http.post<AuthFlowResponse>(`${this.baseUrl}/recovery/verify`, { otp }, { headers: new HttpHeaders({ 'X-Verification-Token': token }), withCredentials: true });
  }
}