import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { User, Transaction } from '../../services/user.service';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  users: User[] = [];
  pendingUsers: User[] = [];
  transactions: Transaction[] = [];
  
  activeTab: 'users' | 'pending' | 'transactions' = 'pending';
  loading: boolean = false;
  error: string = '';
  success: string = '';
  private routerSubscription?: Subscription;
  private refreshInterval?: any;
  private focusHandler = () => this.onWindowFocus();

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadPendingRequests();
    
    // Refresh data when navigating to this route
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        if (event.url === '/admin' || event.urlAfterRedirects === '/admin') {
          this.refreshCurrentTab();
        }
      });
    
    // Refresh when window/tab gets focus
    window.addEventListener('focus', this.focusHandler);
    
    // Auto-refresh every 5 seconds when on pending tab
    this.startAutoRefresh();
  }

  onWindowFocus() {
    // Refresh data when user returns to the tab
    this.refreshCurrentTab();
  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
    window.removeEventListener('focus', this.focusHandler);
    this.stopAutoRefresh();
  }

  startAutoRefresh() {
    this.refreshInterval = setInterval(() => {
      if (this.activeTab === 'pending') {
        this.loadPendingRequests();
      }
    }, 5000); // Refresh every 5 seconds
  }

  stopAutoRefresh() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  refreshCurrentTab() {
    if (this.activeTab === 'pending') {
      this.loadPendingRequests();
    } else if (this.activeTab === 'users') {
      this.loadAllUsers();
    } else if (this.activeTab === 'transactions') {
      this.loadAllTransactions();
    }
  }

  switchTab(tab: 'users' | 'pending' | 'transactions') {
    this.activeTab = tab;
    this.error = '';
    this.success = '';
    
    if (tab === 'pending') {
      this.loadPendingRequests();
    } else if (tab === 'users') {
      this.loadAllUsers();
    } else if (tab === 'transactions') {
      this.loadAllTransactions();
    }
  }

  loadAllUsers() {
    this.loading = true;
    this.error = '';
    this.adminService.getAllUsers().subscribe({
      next: (users: any) => {
        // Map the response to User interface
        this.users = (users || []).map((u: any) => ({
          userId: u.userId,
          fullName: u.fullName,
          accountNumber: u.accountNumber,
          accountType: u.accountType,
          accountStatus: u.accountStatus,
          email: u.email,
          mobileNumber: u.mobileNumber,
          initialDepositAmount: u.initialDepositAmount,
          currentBalance: u.currentBalance || u.initialDepositAmount
        }));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        console.error('Error loading users:', err);
        const errorMessage = err.error?.message || err.message || 'Failed to load users';
        this.error = `Error: ${errorMessage}. Please check your authentication.`;
        this.users = [];
        this.cdr.detectChanges();
      }
    });
  }

  loadPendingRequests() {
    this.loading = true;
    this.error = '';
    
    // Set a timeout to prevent infinite loading
    const timeout = setTimeout(() => {
      if (this.loading) {
        this.loading = false;
        this.error = 'Request timed out. Please check your connection and try again.';
        console.error('Request timeout for pending requests');
      }
    }, 10000); // 10 second timeout
    
    this.adminService.getPendingRequests().subscribe({
      next: (users) => {
        clearTimeout(timeout);
        // Create a new array reference to trigger change detection
        this.pendingUsers = [...(users || [])];
        this.loading = false;
        console.log('Pending requests loaded:', users);
        console.log('Pending users array length:', this.pendingUsers.length);
        if (this.pendingUsers.length === 0) {
          console.log('No pending requests found');
        }
        // Force change detection
        this.cdr.detectChanges();
      },
      error: (err) => {
        clearTimeout(timeout);
        this.loading = false;
        console.error('Error loading pending requests:', err);
        console.error('Error status:', err.status);
        console.error('Error body:', err.error);
        
        let errorMessage = 'Failed to load pending requests';
        if (err.status === 401 || err.status === 403) {
          errorMessage = 'Authentication failed. Please log out and log in again.';
        } else if (err.status === 0) {
          errorMessage = 'Cannot connect to server. Please ensure the backend is running on http://localhost:9990';
        } else if (err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || JSON.stringify(err.error));
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.error = errorMessage;
        this.pendingUsers = [];
        // Force change detection
        this.cdr.detectChanges();
      }
    });
  }

  loadAllTransactions() {
    this.loading = true;
    this.error = '';
    this.adminService.getAllTransactions().subscribe({
      next: (transactions) => {
        // Create a new array reference to trigger change detection
        this.transactions = [...(transactions || [])];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        console.error('Error loading transactions:', err);
        const errorMessage = err.error?.message || err.message || 'Failed to load transactions';
        this.error = `Error: ${errorMessage}. Please check your authentication.`;
        this.transactions = [];
        this.cdr.detectChanges();
      }
    });
  }

  approveAccount(userId: number) {
    if (!userId || userId === 0) {
      this.error = 'Invalid user ID. Cannot approve account.';
      setTimeout(() => this.error = '', 5000);
      return;
    }
    console.log('Approving account for userId:', userId);
    this.adminService.approveAccount(userId).subscribe({
      next: (user) => {
        console.log('Account approved successfully:', user);
        this.success = 'Account approved successfully';
        this.error = '';
        this.loadPendingRequests();
        this.cdr.detectChanges();
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        console.error('Error approving account:', err);
        console.error('Error status:', err.status);
        console.error('Error body:', err.error);
        
        let errorMessage = 'Failed to approve account';
        if (err.status === 401 || err.status === 403) {
          errorMessage = 'Authentication failed. Please log out and log in again.';
        } else if (err.status === 400 && err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || 'Invalid request');
        } else if (err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || JSON.stringify(err.error));
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.error = errorMessage;
        this.success = '';
        this.cdr.detectChanges();
        setTimeout(() => this.error = '', 5000);
      }
    });
  }

  disapproveAccount(userId: number) {
    console.log('Disapproving account for userId:', userId);
    this.adminService.disapproveAccount(userId).subscribe({
      next: (user) => {
        console.log('Account disapproved successfully:', user);
        this.success = 'Account disapproved successfully';
        this.error = '';
        this.loadPendingRequests();
        this.cdr.detectChanges();
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        console.error('Error disapproving account:', err);
        console.error('Error status:', err.status);
        console.error('Error body:', err.error);
        
        let errorMessage = 'Failed to disapprove account';
        if (err.status === 401 || err.status === 403) {
          errorMessage = 'Authentication failed. Please log out and log in again.';
        } else if (err.status === 400 && err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || 'Invalid request');
        } else if (err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || JSON.stringify(err.error));
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.error = errorMessage;
        this.success = '';
        this.cdr.detectChanges();
        setTimeout(() => this.error = '', 5000);
      }
    });
  }

  deleteTransaction(transactionId: number) {
    if (confirm('Are you sure you want to delete this transaction?')) {
      this.adminService.deleteTransaction(transactionId).subscribe({
        next: () => {
          this.success = 'Transaction deleted successfully';
          this.loadAllTransactions();
          setTimeout(() => this.success = '', 3000);
        },
        error: (err) => {
          this.error = 'Failed to delete transaction';
          setTimeout(() => this.error = '', 3000);
        }
      });
    }
  }

  softDeleteUser(userId: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.adminService.softDeleteUser(userId).subscribe({
        next: () => {
          this.success = 'User deleted successfully';
          this.loadAllUsers();
          setTimeout(() => this.success = '', 3000);
        },
        error: (err) => {
          this.error = 'Failed to delete user';
          setTimeout(() => this.error = '', 3000);
        }
      });
    }
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}
