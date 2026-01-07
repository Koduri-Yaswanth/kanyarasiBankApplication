package com.tcs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.dto.AccountCreationRequest;
import com.tcs.dto.TransactionRequest;
import com.tcs.dto.TransactionResponse;
import com.tcs.dto.UserResponse;
import com.tcs.entity.Transaction;
import com.tcs.exception.DuplicateUserException;
import com.tcs.exception.InvalidAccountType;
import com.tcs.exception.LowAmountException;
import com.tcs.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserService userService;
	
	@PostMapping("/create-account")
	public ResponseEntity<UserResponse> createAccount(@RequestBody AccountCreationRequest request) 
			throws DuplicateUserException, LowAmountException, InvalidAccountType {
		UserResponse response = userService.createAccountRequest(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/transaction")
	public ResponseEntity<?> makeTransaction(@RequestBody TransactionRequest request) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			TransactionResponse response = userService.makeTransaction(username, request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}
	
	@GetMapping("/transaction-history")
	public ResponseEntity<List<Transaction>> getTransactionHistory() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			List<Transaction> transactions = userService.getTransactionHistory(username);
			return ResponseEntity.ok(transactions);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/account-info")
	public ResponseEntity<?> getAccountInfo() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
				return ResponseEntity.status(401).body("Unauthorized: Please provide Basic Authentication credentials (username and password)");
			}
			String username = authentication.getName();
			com.tcs.entity.User user = userService.getUserByUsername(username);
			if (user == null) {
				return ResponseEntity.badRequest().body("Account not found for username: " + username);
			}
			
			// Calculate current balance
			double currentBalance = userService.calculateBalance(user.getUserId());
			
			// Build a formatted account info string
			StringBuilder accountInfo = new StringBuilder();
			accountInfo.append("Username: ").append(username).append("\n");
			accountInfo.append("Full Name: ").append(user.getFullName()).append("\n");
			accountInfo.append("Account Number: ").append(user.getAccountNumber()).append("\n");
			accountInfo.append("Account Type: ").append(user.getAccountType()).append("\n");
			accountInfo.append("Account Status: ").append(user.getAccountStatus()).append("\n");
			accountInfo.append("Current Balance: ₹").append(String.format("%.2f", currentBalance)).append("\n");
			accountInfo.append("IFSC Code: ").append(user.getIfscCode() != null ? user.getIfscCode() : "N/A").append("\n");
			accountInfo.append("Branch: ").append(user.getBranchName() != null ? user.getBranchName() : "N/A").append("\n");
			accountInfo.append("Email: ").append(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : "N/A").append("\n");
			accountInfo.append("Mobile: ").append(user.getMobileNumber()).append("\n");
			
			return ResponseEntity.ok().body(accountInfo.toString());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}
}
