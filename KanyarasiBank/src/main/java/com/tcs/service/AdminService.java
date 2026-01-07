package com.tcs.service;

import java.util.List;

import com.tcs.entity.Transaction;
import com.tcs.entity.User;

public interface AdminService {
	public User approveUserAccount(Long userId);
	public User disapproveUserAccount(Long userId);
	public List<User> getAllUsers();
	public List<User> getPendingAccountRequests();
	public List<Transaction> getAllTransactions();
	public void deleteTransaction(Long transactionId);
	public User softDeleteUser(Long userId);
	public double calculateUserBalance(Long userId);
}
