import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface User {
  userId?: number;
  fullName: string;
  gender?: string;
  dob?: string;
  nationality?: string;
  mobileNumber: number;
  email: string;
  address?: string;
  aadhar?: number;
  pan?: string;
  accountType: string;
  initialDepositAmount: number;
  accountNumber?: number;
  accountStatus?: string;
  currentBalance?: number;
}

export interface AccountCreationRequest {
  user: User;
  username: string;
  password: string;
  transactionPin: string;
}

export interface UserResponse {
  fullName: string;
  accountNumber: number;
  ifscCode: string;
  totalBalance: number;
  createdDate: string;
  message: string;
}

export interface TransactionRequest {
  transactionType: string;
  amount: number;
  description: string;
  transactionPin: string;
  toAccountNumber?: number;
}

export interface Transaction {
  transactionId?: number;
  transactionType: string;
  amount: number;
  description?: string;
  transactionDate?: string;
  status?: string;
  toAccountNumber?: number;
}

export interface TransactionResponse {
  transactionId: number;
  transactionType: string;
  amount: number;
  description: string;
  transactionDate: string;
  status: string;
  toAccountNumber?: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:9990/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  createAccount(request: AccountCreationRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.apiUrl}/user/create-account`, request);
  }

  makeTransaction(request: TransactionRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.apiUrl}/user/transaction`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getTransactionHistory(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/user/transaction-history`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAccountInfo(): Observable<string> {
    const headers = this.authService.getAuthHeaders();
    console.log('UserService: Making account-info request');
    console.log('UserService: URL:', `${this.apiUrl}/user/account-info`);
    console.log('UserService: Headers:', Array.from(headers.keys()));
    console.log('UserService: Auth header present:', headers.has('Authorization'));
    if (headers.has('Authorization')) {
      console.log('UserService: Authorization header value:', headers.get('Authorization')?.substring(0, 20) + '...');
    }
    
    return this.http.get(`${this.apiUrl}/user/account-info`, {
      headers: headers,
      responseType: 'text'
    }).pipe(
      map((response: string) => {
        console.log('UserService: Raw response received');
        console.log('UserService: Response type:', typeof response);
        console.log('UserService: Response length:', response?.length);
        console.log('UserService: Response content:', response);
        return response || '';
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('UserService: Error in getAccountInfo');
        console.error('UserService: Error status:', error.status);
        console.error('UserService: Error statusText:', error.statusText);
        console.error('UserService: Error message:', error.message);
        console.error('UserService: Error error:', error.error);
        
        // For text responses, error.error might be a string
        let errorMessage = 'Failed to load account information';
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.error.message) {
            errorMessage = error.error.message;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        return throwError(() => ({
          status: error.status,
          statusText: error.statusText,
          message: errorMessage,
          error: error.error
        }));
      })
    );
  }
}

