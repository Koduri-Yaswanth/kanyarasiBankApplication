import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { User, Transaction } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:9990/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/admin/users`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getPendingRequests(): Observable<User[]> {
    const headers = this.authService.getAuthHeaders();
    console.log('Fetching pending requests with headers:', headers.keys());
    return this.http.get<User[]>(`${this.apiUrl}/admin/pending-requests`, {
      headers: headers,
      observe: 'body',
      responseType: 'json'
    });
  }

  approveAccount(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/approve-account/${userId}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  disapproveAccount(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/disapprove-account/${userId}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/admin/transactions`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteTransaction(transactionId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/transaction/${transactionId}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  softDeleteUser(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/soft-delete-user/${userId}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }
}

