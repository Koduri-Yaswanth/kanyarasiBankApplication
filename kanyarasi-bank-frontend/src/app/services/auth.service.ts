import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:9990/api';
  private currentUser: string | null = null;

  constructor(private http: HttpClient) {
    this.currentUser = localStorage.getItem('currentUser');
  }

  login(credentials: LoginRequest): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    return this.http.post(`${this.apiUrl}/auth/login`, credentials, { headers }).pipe(
      tap((response: any) => {
        this.currentUser = credentials.username;
        localStorage.setItem('currentUser', credentials.username);
        localStorage.setItem('auth', btoa(`${credentials.username}:${credentials.password}`));
        if (response?.role) {
          localStorage.setItem('userRole', response.role);
        }
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/logout`, {}).pipe(
      tap(() => {
        this.currentUser = null;
        localStorage.removeItem('currentUser');
        localStorage.removeItem('auth');
        localStorage.removeItem('userRole');
      })
    );
  }

  getCurrentUser(): string | null {
    return this.currentUser;
  }

  isAuthenticated(): boolean {
    return this.currentUser !== null;
  }

  getAuthHeaders(): HttpHeaders {
    const auth = localStorage.getItem('auth');
    if (auth) {
      return new HttpHeaders({
        'Authorization': 'Basic ' + auth,
        'Content-Type': 'application/json'
      });
    }
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }
}

