import { HttpClient } from '@angular/common/http';
import { Injectable, Service } from '@angular/core';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class Auth {

     private baseUrl = 'http://localhost:8080/auth/v1';

    constructor(private http: HttpClient) {}

register(data:any) : Observable<any>{
    return this.http.post( `${this.baseUrl}/register`,data);
}
login(data:any) : Observable<any>{
    return this.http.post( `${this.baseUrl}/login`,data);       
}
 verifyOtp(otp: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/verified-otp`,
      { otp }
    );   
}   
 

}
