package com.tcs.service;

import java.util.List;

import com.tcs.dto.AccountCreationRequest;
import com.tcs.dto.TransactionRequest;
import com.tcs.dto.TransactionResponse;
import com.tcs.dto.UserResponse;
import com.tcs.entity.Transaction;
import com.tcs.entity.User;
import com.tcs.exception.DuplicateUserException;
import com.tcs.exception.InvalidAccountType;
import com.tcs.exception.LowAmountException;

public interface UserService {
	public UserResponse createAccountRequest(AccountCreationRequest request) throws DuplicateUserException, LowAmountException, InvalidAccountType;
	public TransactionResponse makeTransaction(String username, TransactionRequest request) throws Exception;
	public List<Transaction> getTransactionHistory(String username);
	public User getUserByUsername(String username);
	public double calculateBalance(Long userId);
}
