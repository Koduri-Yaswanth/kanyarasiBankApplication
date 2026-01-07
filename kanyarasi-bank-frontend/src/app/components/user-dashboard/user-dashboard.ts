import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, Transaction, TransactionRequest } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.css'
})
export class UserDashboardComponent implements OnInit {
  accountInfo: string = '';
  currentBalance: number | null = null;
  transactions: Transaction[] = [];
  transactionRequest: TransactionRequest = {
    transactionType: 'DEPOSIT',
    amount: 0,
    description: '',
    transactionPin: '',
    toAccountNumber: undefined
  };
  
  showTransactionForm: boolean = false;
  error: string = '';
  success: string = '';
  loading: boolean = false;
  loadingAccountInfo: boolean = false;
  loadingTransactions: boolean = false;

  transactionTypes = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'];

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadAccountInfo();
    this.loadTransactions();
  }

  loadAccountInfo() {
    this.loadingAccountInfo = true;
    this.error = '';
    this.accountInfo = '';
    console.log('Loading account info...');
    console.log('Auth headers:', this.authService.getAuthHeaders().keys());
    
    this.userService.getAccountInfo().subscribe({
      next: (info: string) => {
        this.loadingAccountInfo = false;
        console.log('Account info received - Type:', typeof info);
        console.log('Account info received - Value:', info);
        console.log('Account info received - Length:', info?.length);
        
        if (!info || info.trim().length === 0) {
          console.warn('Account info is empty or null');
          this.accountInfo = 'No account information available';
          this.error = 'No account information received from server';
          return;
        }
        
        // Set account info
        this.accountInfo = info.trim();
        this.error = ''; // Clear any previous errors
        console.log('Account info set:', this.accountInfo);
        
        // Extract balance from the string if present
        const balanceMatch = info.match(/Current Balance: ₹([\d.]+)/);
        if (balanceMatch) {
          this.currentBalance = parseFloat(balanceMatch[1]);
          console.log('Extracted balance:', this.currentBalance);
        } else {
          // Try alternative format
          const altMatch = info.match(/Balance[:\s]+₹?([\d.]+)/i);
          if (altMatch) {
            this.currentBalance = parseFloat(altMatch[1]);
            console.log('Extracted balance (alt):', this.currentBalance);
          }
        }
        
        console.log('Final account info:', this.accountInfo);
        console.log('Final current balance:', this.currentBalance);
        
        // Force change detection to update the view
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loadingAccountInfo = false;
        console.error('Error loading account info:', err);
        console.error('Error status:', err.status);
        console.error('Error body:', err.error);
        
        let errorMessage = 'Failed to load account information';
        if (err.status === 401 || err.status === 403) {
          errorMessage = 'Authentication failed. Please log out and log in again.';
        } else if (err.status === 0) {
          errorMessage = 'Cannot connect to server. Please ensure the backend is running.';
        } else if (err.error) {
          errorMessage = typeof err.error === 'string' ? err.error : (err.error.message || 'Failed to load account information');
        }
        
        this.error = errorMessage;
        this.accountInfo = '';
        
        // Force change detection to update the view
        this.cdr.detectChanges();
      }
    });
  }

  loadTransactions() {
    this.loadingTransactions = true;
    this.userService.getTransactionHistory().subscribe({
      next: (transactions) => {
        this.transactions = transactions || [];
        this.loadingTransactions = false;
        // Balance should come from account info, but if not available, we can calculate
        // (This is a fallback - backend should always provide balance)
        
        // Force change detection to update the view
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loadingTransactions = false;
        this.error = 'Failed to load transaction history';
        
        // Force change detection to update the view
        this.cdr.detectChanges();
      }
    });
  }

  toggleTransactionForm() {
    this.showTransactionForm = !this.showTransactionForm;
    this.error = '';
    this.success = '';
    if (this.showTransactionForm) {
      this.transactionRequest = {
        transactionType: 'DEPOSIT',
        amount: 0,
        description: '',
        transactionPin: '',
        toAccountNumber: undefined
      };
    }
  }

  submitTransaction() {
    if (!this.transactionRequest.amount || this.transactionRequest.amount <= 0) {
      this.error = 'Please enter a valid amount';
      return;
    }
    if (!this.transactionRequest.transactionPin || this.transactionRequest.transactionPin.length !== 4) {
      this.error = 'Please enter a valid 4-digit transaction PIN';
      return;
    }
    if (this.transactionRequest.transactionType === 'TRANSFER' && !this.transactionRequest.toAccountNumber) {
      this.error = 'Please enter recipient account number for transfer';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.userService.makeTransaction(this.transactionRequest).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = response.message || 'Transaction completed successfully';
        this.showTransactionForm = false;
        // Reload account info to get updated balance
        this.loadAccountInfo();
        // Reload transactions to show new transaction
        this.loadTransactions();
        // Clear form
        this.transactionRequest = {
          transactionType: 'DEPOSIT',
          amount: 0,
          description: '',
          transactionPin: '',
          toAccountNumber: undefined
        };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error || 'Transaction failed. Please try again.';
      }
    });
  }

  calculateBalanceFromTransactions() {
    // This is a fallback - balance should come from backend
    // Calculate from initial deposit + transactions
    let balance = 0;
    // Start with initial deposit (would need to get from account info)
    // For now, just calculate from transactions
    for (let transaction of this.transactions) {
      if (transaction.status === 'SUCCESS') {
        if (transaction.transactionType === 'DEPOSIT') {
          balance += transaction.amount;
        } else if (transaction.transactionType === 'WITHDRAWAL' || transaction.transactionType === 'TRANSFER') {
          balance -= transaction.amount;
        }
      }
    }
    // This is approximate - backend should provide accurate balance
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
