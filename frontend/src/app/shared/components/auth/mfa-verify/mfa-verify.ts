import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../register/service/auth';
import { LoginTokens } from '../register/models/auth-flow-response.model';

@Component({ selector:'app-mfa-verify', standalone:true, imports:[CommonModule,FormsModule,RouterLink], templateUrl:'./mfa-verify.html', styleUrl:'./mfa-verify.scss' })
export class MfaVerifyComponent implements OnInit {
 verificationToken=''; email=''; otp=''; loading=false; error='';
 constructor(private auth:Auth,private router:Router){}
 ngOnInit():void { this.verificationToken=sessionStorage.getItem('mfaVerificationToken')||'';this.email=sessionStorage.getItem('mfaEmail')||'';if(!this.verificationToken||sessionStorage.getItem('mfaStatus')!=='MFA_REQUIRED')this.router.navigate(['/login']); }
 verify():void { if(!/^\d{6}$/.test(this.otp.trim())){this.error='Enter a valid 6-digit authenticator code.';return;}this.loading=true;this.error='';this.auth.verifyAuthenticatorLogin(this.verificationToken,this.otp.trim()).subscribe({next:t=>this.finish(t),error:e=>{this.loading=false;this.error=e?.error?.message||'Invalid or expired authenticator code.';}}); }
 private finish(t:LoginTokens):void {this.loading=false;if(!t?.accessToken){this.error='Access token was not returned.';return;}sessionStorage.setItem('accessToken',t.accessToken);sessionStorage.setItem('refreshToken',t.refreshToken||'');sessionStorage.setItem('email',this.email);const role=this.role(t.accessToken);sessionStorage.setItem('role',role);sessionStorage.removeItem('mfaVerificationToken');sessionStorage.removeItem('mfaStatus');sessionStorage.removeItem('mfaEmail');this.router.navigate([role==='loanofficer'?'/loan-officer/dashboard':'/customer/dashboard']);}
 private role(token:string):string{try{const p=JSON.parse(atob(token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/')));return String(p.role||p.roles||'customer').toLowerCase().replace(/^role_/,'').replace(/^\[|\]$/g,'');}catch{return 'customer';}}
}
