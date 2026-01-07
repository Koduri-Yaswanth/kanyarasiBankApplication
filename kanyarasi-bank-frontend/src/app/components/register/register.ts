import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, User, AccountCreationRequest } from '../../services/user.service';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  user: User = {
    fullName: '',
    gender: '',
    dob: '',
    nationality: '',
    mobileNumber: 0,
    email: '',
    address: '',
    aadhar: 0,
    pan: '',
    accountType: '',
    initialDepositAmount: 0
  };
  
  username: string = '';
  password: string = '';
  transactionPin: string = '';
  error: string = '';
  success: string = '';
  loading: boolean = false;

  accountTypes = ['savings', 'current', 'salary', 'student'];
  genders = ['Male', 'Female', 'Other'];
  nationalities = ['Indian', 'Other'];

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const request: AccountCreationRequest = {
      user: this.user,
      username: this.username,
      password: this.password,
      transactionPin: this.transactionPin
    };

    this.userService.createAccount(request).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = `Account created successfully! Account Number: ${response.accountNumber}`;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || err.error || 'Failed to create account. Please try again.';
      }
    });
  }

  validateForm(): boolean {
    if (!this.user.fullName || !this.username || !this.password || !this.transactionPin) {
      this.error = 'Please fill all required fields';
      return false;
    }
    if (this.user.initialDepositAmount < 1000) {
      this.error = 'Initial deposit must be at least ₹1000';
      return false;
    }
    if (this.transactionPin.length !== 4) {
      this.error = 'Transaction PIN must be 4 digits';
      return false;
    }
    return true;
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
