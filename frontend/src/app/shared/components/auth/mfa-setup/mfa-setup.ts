import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../register/service/auth';
import { AuthFlowResponse, LoginTokens } from '../register/models/auth-flow-response.model';
@Component({ selector: 'app-mfa-setup', standalone: true, imports: [CommonModule, FormsModule, RouterLink], templateUrl: './mfa-setup.html', styleUrl: './mfa-setup.scss' })
export class MfaSetupComponent implements OnInit {
  verificationToken = ''; email = ''; setupUri = ''; qrCodeDataUrl = ''; otp = ''; loading = false; error = ''; notice = '';
  constructor(private auth: Auth, private router: Router) {}
  ngOnInit(): void {
    this.verificationToken = sessionStorage.getItem('mfaVerificationToken') || ''; this.email = sessionStorage.getItem('mfaEmail') || '';
    const status = sessionStorage.getItem('mfaStatus') || '';
    if (!this.verificationToken || !['MFA_SETUP_REQUIRED','MFA_RECOVERY_VERIFIED'].includes(status)) { this.router.navigate(['/login']); return; }
    this.loadSetup();
  }
  loadSetup(): void { this.loading = true; this.auth.setupAuthenticator(this.verificationToken).subscribe({ next: (r: AuthFlowResponse) => { this.loading=false; this.setupUri=r.setupUri||''; this.qrCodeDataUrl=r.qrCodeDataUrl||''; this.notice=r.message||'Scan the QR code, then enter the 6-digit code.'; }, error: e => { this.loading=false; this.error=e?.error?.message||'Could not load QR code.'; } }); }
  copySetupUri(): void { if (navigator.clipboard && this.setupUri) navigator.clipboard.writeText(this.setupUri).then(()=>this.notice='Setup link copied.'); }
  verifySetup(): void { if (!/^\d{6}$/.test(this.otp.trim())) { this.error='Enter a valid 6-digit code.'; return; } this.loading=true; this.error=''; this.auth.verifyAuthenticatorSetup(this.verificationToken,this.otp.trim()).subscribe({ next: (t: LoginTokens)=>this.finish(t), error:e=>{this.loading=false;this.error=e?.error?.message||'Invalid authenticator code.';} }); }
  private finish(t: LoginTokens): void { this.loading=false; if(!t?.accessToken){this.error='Access token was not returned.';return;} sessionStorage.setItem('accessToken',t.accessToken);sessionStorage.setItem('refreshToken',t.refreshToken||'');sessionStorage.setItem('email',this.email);sessionStorage.removeItem('mfaVerificationToken');sessionStorage.removeItem('mfaStatus');sessionStorage.removeItem('mfaEmail'); const role=this.role(t.accessToken);sessionStorage.setItem('role',role);this.router.navigate([role==='loanofficer'?'/loan-officer/dashboard':'/customer/dashboard']); }
  private role(token:string):string { try { const p=JSON.parse(atob(token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/')));return String(p.role||p.roles||'customer').toLowerCase().replace(/^role_/,'').replace(/^\[|\]$/g,''); } catch{return 'customer';} }
}
