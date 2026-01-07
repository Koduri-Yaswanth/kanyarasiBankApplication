import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  error: string = '';
  loading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.username || !this.password) {
      this.error = 'Please enter both username and password';
      return;
    }

    this.loading = true;
    this.error = '';

    console.log('Attempting login for username:', this.username);

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response: any) => {
        this.loading = false;
        console.log('Login successful, response:', response);
        // Check role from backend response
        const role = response?.role || '';
        console.log('User role:', role);
        if (role.includes('ADMIN') || role === 'ADMIN' || role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/user']);
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('Login error:', err);
        console.error('Error status:', err.status);
        console.error('Error body:', err.error);
        
        let errorMessage = 'Invalid username or password';
        if (err.status === 0) {
          errorMessage = 'Cannot connect to server. Please ensure the backend is running on http://localhost:9990';
        } else if (err.status === 401 || err.status === 403) {
          errorMessage = 'Invalid username or password. Please check your credentials.';
        } else if (err.error) {
          const errorText = typeof err.error === 'string' ? err.error : (err.error.message || 'Invalid username or password');
          
          // Check if it's an account rejected message
          if (errorText.includes('ACCOUNT_REJECTED') || errorText.includes('rejected')) {
            // Extract the message after ACCOUNT_REJECTED: or use the full message
            if (errorText.includes('ACCOUNT_REJECTED:')) {
              errorMessage = errorText.split('ACCOUNT_REJECTED:')[1].trim();
            } else {
              errorMessage = 'Your account request has been rejected. Please contact your nearest Kanyarasi bank branch.';
            }
          } else {
            errorMessage = errorText;
          }
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.error = errorMessage;
      }
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
