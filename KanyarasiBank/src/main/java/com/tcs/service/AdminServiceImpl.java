package com.tcs.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.entity.Transaction;
import com.tcs.entity.User;
import com.tcs.repository.TransactionRepository;
import com.tcs.repository.UserRepository;
import com.tcs.service.UserService;

@Service
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public User approveUserAccount(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}
		
		User user = userOpt.get();
		if (!"pending".equalsIgnoreCase(user.getAccountStatus())) {
			throw new RuntimeException("User account is not in pending status");
		}
		
		user.setAccountStatus("approved");
		user.setUpdatedAt(LocalDateTime.now());
		
		// Create initial deposit transaction
		if (user.getInitialDepositAmount() > 0) {
			Transaction initialDeposit = new Transaction();
			initialDeposit.setUser(user);
			initialDeposit.setTransactionType("DEPOSIT");
			initialDeposit.setAmount(user.getInitialDepositAmount());
			initialDeposit.setDescription("Initial deposit");
			initialDeposit.setTransactionDate(LocalDateTime.now());
			initialDeposit.setStatus("SUCCESS");
			transactionRepository.save(initialDeposit);
		}
		
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User disapproveUserAccount(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}
		
		User user = userOpt.get();
		if (!"pending".equalsIgnoreCase(user.getAccountStatus())) {
			throw new RuntimeException("User account is not in pending status");
		}
		
		user.setAccountStatus("rejected");
		user.setUpdatedAt(LocalDateTime.now());
		
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findByIsDeleatedFalse();
	}

	@Override
	public List<User> getPendingAccountRequests() {
		return userRepository.findByAccountStatus("pending");
	}

	@Override
	public List<Transaction> getAllTransactions() {
		return transactionRepository.findAllByOrderByTransactionDateDesc();
	}

	@Override
	@Transactional
	public void deleteTransaction(Long transactionId) {
		Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
		if (transactionOpt.isEmpty()) {
			throw new RuntimeException("Transaction not found");
		}
		transactionRepository.deleteById(transactionId);
	}

	@Override
	@Transactional
	public User softDeleteUser(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}
		
		User user = userOpt.get();
		user.setDeleated(true);
		user.setAccountStatus("closed");
		user.setUpdatedAt(LocalDateTime.now());
		
		return userRepository.save(user);
	}
	
	@Override
	public double calculateUserBalance(Long userId) {
		return userService.calculateBalance(userId);
	}
}
