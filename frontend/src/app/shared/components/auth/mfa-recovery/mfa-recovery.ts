import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../register/service/auth';
import { AuthFlowResponse } from '../register/models/auth-flow-response.model';

@Component({selector:'app-mfa-recovery',standalone:true,imports:[CommonModule,FormsModule,RouterLink],templateUrl:'./mfa-recovery.html',styleUrl:'./mfa-recovery.scss'})
export class MfaRecoveryComponent implements OnInit {
 verificationToken='';email='';otp='';requested=false;loading=false;error='';notice='';
 constructor(private auth:Auth,private router:Router){}
 ngOnInit():void{this.verificationToken=sessionStorage.getItem('mfaVerificationToken')||'';this.email=sessionStorage.getItem('mfaEmail')||'';if(!this.verificationToken||sessionStorage.getItem('mfaStatus')!=='MFA_REQUIRED')this.router.navigate(['/login']);}
 requestOtp():void{this.loading=true;this.error='';this.auth.requestRecoveryOtp(this.verificationToken).subscribe({next:r=>{this.loading=false;this.requested=true;this.notice=r?.message||'Recovery OTP sent to your registered email.';},error:e=>{this.loading=false;this.error=e?.error?.message||'Could not send recovery OTP.';}});}
 verifyOtp():void{if(!/^\d{6}$/.test(this.otp.trim())){this.error='Enter the 6-digit email OTP.';return;}this.loading=true;this.error='';this.auth.verifyRecoveryOtp(this.verificationToken,this.otp.trim()).subscribe({next:(r:AuthFlowResponse)=>{if(!r.verificationToken){this.loading=false;this.error='Recovery response did not include a setup token.';return;}sessionStorage.setItem('mfaVerificationToken',r.verificationToken);sessionStorage.setItem('mfaStatus',r.status||'MFA_RECOVERY_VERIFIED');this.router.navigate(['/mfa-setup']);},error:e=>{this.loading=false;this.error=e?.error?.message||'Recovery OTP is invalid or expired.';}});}
}
