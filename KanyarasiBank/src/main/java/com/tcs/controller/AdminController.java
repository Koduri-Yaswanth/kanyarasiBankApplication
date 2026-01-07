package com.tcs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tcs.entity.Transaction;
import com.tcs.entity.User;
import com.tcs.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@PutMapping("/approve-account/{userId}")
	public ResponseEntity<?> approveUserAccount(@PathVariable Long userId) {
		try {
			User user = adminService.approveUserAccount(userId);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: Failed to approve account - " + e.getMessage());
		}
	}

	@PutMapping("/disapprove-account/{userId}")
	public ResponseEntity<?> disapproveUserAccount(@PathVariable Long userId) {
		try {
			User user = adminService.disapproveUserAccount(userId);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: Failed to disapprove account - " + e.getMessage());
		}
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<User> users = adminService.getAllUsers();
		// Add balance to each user
		List<Map<String, Object>> usersWithBalance = new ArrayList<>();
		for (User user : users) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("userId", user.getUserId());
			userMap.put("fullName", user.getFullName());
			userMap.put("accountNumber", user.getAccountNumber());
			userMap.put("accountType", user.getAccountType());
			userMap.put("accountStatus", user.getAccountStatus());
			userMap.put("email", user.getEmail());
			userMap.put("mobileNumber", user.getMobileNumber());
			userMap.put("initialDepositAmount", user.getInitialDepositAmount());
			// Calculate current balance
			double balance = adminService.calculateUserBalance(user.getUserId());
			userMap.put("currentBalance", balance);
			usersWithBalance.add(userMap);
		}
		return ResponseEntity.ok(usersWithBalance);
	}

	@GetMapping("/pending-requests")
	public ResponseEntity<List<User>> getPendingAccountRequests() {
		List<User> pendingUsers = adminService.getPendingAccountRequests();
		return ResponseEntity.ok(pendingUsers);
	}

	@GetMapping("/transactions")
	public ResponseEntity<List<Transaction>> getAllTransactions() {
		List<Transaction> transactions = adminService.getAllTransactions();
		return ResponseEntity.ok(transactions);
	}

	@DeleteMapping("/transaction/{transactionId}")
	public ResponseEntity<?> deleteTransaction(@PathVariable Long transactionId) {
		try {
			adminService.deleteTransaction(transactionId);
			return ResponseEntity.ok().body("Transaction deleted successfully");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to delete transaction");
		}
	}

	@PutMapping("/soft-delete-user/{userId}")
	public ResponseEntity<User> softDeleteUser(@PathVariable Long userId) {
		try {
			User user = adminService.softDeleteUser(userId);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
