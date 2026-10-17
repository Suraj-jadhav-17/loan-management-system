import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Auth } from '../register/service/auth';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  email = '';
  password = '';
  message = '';
  loading = false;

  constructor(private readonly auth: Auth, private readonly router: Router, private readonly route: ActivatedRoute) {
    if (this.route.snapshot.queryParamMap.get('verified') === 'true') {
      this.message = 'Email verified successfully. Please sign in.';
    }
  }

  login(): void {
    this.message = '';
    if (!this.email.trim() || !this.password) {
      this.message = 'Please enter your email and password.';
      return;
    }

    this.loading = true;
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    sessionStorage.removeItem('role');

    this.auth.login({ email: this.email.trim().toLowerCase(), password: this.password }).subscribe({
      next: response => {
        this.loading = false;
        if (!response?.status || !response.verificationToken) {
          this.message = response?.message || 'Login response is missing the verification token.';
          return;
        }
        sessionStorage.setItem('mfaVerificationToken', response.verificationToken);
        sessionStorage.setItem('mfaStatus', response.status);
        sessionStorage.setItem('mfaEmail', response.email || this.email.trim().toLowerCase());
        this.router.navigate(['/mfa']);
      },
      error: error => {
        this.loading = false;
        const body = error?.error;
        this.message = body?.message || (typeof body === 'string' ? body : 'Login failed. Check your email and password.');
      }
    });
  }
}