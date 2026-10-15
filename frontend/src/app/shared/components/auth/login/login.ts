
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../../../features/loan-deals/services/auth/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {

  email = '';
  password = '';
  message = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  login(): void{
    const data ={
      email: this.email,
      password: this.password
    };
    this.authService.login(data).subscribe({
      next: (response) => {
        // Handle successful login
        console.log('Login successful:', response);
           this.message = 'Login successful';

        if (response.accessToken) {
          sessionStorage.setItem(
            'accessToken',
            response.accessToken
          );
        }

        this.router.navigate(['/profile']); // Navigate to dashboard or desired route
      },
        error: (error) => {
        console.error(error);
        this.message =
          error.error?.message || 'Login failed';
      }

    });

  }
}

