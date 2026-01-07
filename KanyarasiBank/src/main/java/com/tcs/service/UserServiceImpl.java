package com.tcs.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.dto.AccountCreationRequest;
import com.tcs.dto.TransactionRequest;
import com.tcs.dto.TransactionResponse;
import com.tcs.dto.UserResponse;
import com.tcs.entity.Account;
import com.tcs.entity.Transaction;
import com.tcs.entity.User;
import com.tcs.exception.DuplicateUserException;
import com.tcs.exception.InvalidAccountType;
import com.tcs.exception.LowAmountException;
import com.tcs.repository.AccountRepository;
import com.tcs.repository.TransactionRepository;
import com.tcs.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserResponse userResponse;

	@Override
	@Transactional
	public UserResponse createAccountRequest(AccountCreationRequest request) 
			throws DuplicateUserException, LowAmountException, InvalidAccountType {
		
		User user = request.getUser();
		UserResponse ur = new UserResponse();
		
		// Check if user with same aadhar exists
		User existingUser = userRepository.findByAadhar(user.getAadhar());
		if (existingUser != null) {
			throw new DuplicateUserException("User with the aadhar already exists.");
		}
		
		// Check if username already exists
		if (accountRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateUserException("Username already exists.");
		}
		
		// Validate account type
		if (!user.getAccountType().equalsIgnoreCase("savings") && 
			!user.getAccountType().equalsIgnoreCase("current") && 
			!user.getAccountType().equalsIgnoreCase("salary") && 
			!user.getAccountType().equalsIgnoreCase("student")) {
			throw new InvalidAccountType("Enter valid account type.");
		}
		
		// Validate initial deposit
		if (user.getInitialDepositAmount() < 1000) {
			throw new LowAmountException("Initial Deposit must be >=1000");
		}
		
		// Generate account number
		SecureRandom random = new SecureRandom();
		long random12Digit = 100000000000L + (Math.abs(random.nextLong()) % 900000000000L);
		
		// Create user with pending status
		User newUser = new User(user.getFullName(), user.getGender(), user.getDob(), 
				user.getNationality(), user.getMobileNumber(), user.getEmail(), 
				user.getAddress(), user.getAadhar(), user.getPan(), 
				user.getAccountType(), user.getInitialDepositAmount());
		newUser.setAccountNumber(random12Digit);
		newUser.setBranchName("main branch");
		newUser.setIfscCode("TCSIF0223X");
		newUser.setAccountStatus("pending"); // Set to pending for admin approval
		newUser.setCreatedAt(LocalDateTime.now());
		newUser.setUpdatedAt(LocalDateTime.now());
		newUser.setDeleated(false);
		
		User savedUser = userRepository.save(newUser);
		
		// Create account with encrypted password
		Account account = new Account();
		account.setUsername(request.getUsername());
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setTransactionPin(passwordEncoder.encode(request.getTransactionPin()));
		account.setUser(savedUser);
		account.setRole("ROLE_USER");
		accountRepository.save(account);
		
		// Create initial deposit transaction if approved (but status is pending, so no transaction yet)
		
		ur.setFullName(user.getFullName());
		ur.setAccountNumber(random12Digit);
		ur.setIfscCode("TCSIF0223X");
		ur.setTotalBalance(user.getInitialDepositAmount());
		ur.setCreatedDate(LocalDate.now());
		ur.setMessage("Account creation request submitted. Waiting for admin approval.");
		
		return ur;
	}

	@Override
	@Transactional
	public TransactionResponse makeTransaction(String username, TransactionRequest request) throws Exception {
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			throw new Exception("Account not found");
		}
		
		User user = account.getUser();
		
		// Check if account is approved and active
		if (!"approved".equalsIgnoreCase(user.getAccountStatus()) && !"active".equalsIgnoreCase(user.getAccountStatus())) {
			throw new Exception("Account is not approved or active. Cannot perform transactions.");
		}
		
		// Verify transaction pin
		if (!passwordEncoder.matches(request.getTransactionPin(), account.getTransactionPin())) {
			throw new Exception("Invalid transaction PIN");
		}
		
		// Get current balance (stored in user's initialDepositAmount, but we need to calculate from transactions)
		double currentBalance = calculateCurrentBalance(user.getUserId());
		
		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setTransactionType(request.getTransactionType().toUpperCase());
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setToAccountNumber(request.getToAccountNumber());
		
		String transactionType = request.getTransactionType().toUpperCase();
		String message = "";
		
		if ("DEPOSIT".equals(transactionType)) {
			transaction.setStatus("SUCCESS");
			message = "Deposit successful";
		} else if ("WITHDRAWAL".equals(transactionType)) {
			if (currentBalance < request.getAmount()) {
				transaction.setStatus("FAILED");
				throw new Exception("Insufficient balance");
			}
			transaction.setStatus("SUCCESS");
			message = "Withdrawal successful";
		} else if ("TRANSFER".equals(transactionType)) {
			if (currentBalance < request.getAmount()) {
				transaction.setStatus("FAILED");
				transactionRepository.save(transaction);
				throw new Exception("Insufficient balance");
			}
			if (request.getToAccountNumber() == null) {
				transaction.setStatus("FAILED");
				transactionRepository.save(transaction);
				throw new Exception("To account number is required for transfer");
			}
			// Check if trying to transfer to own account
			if (request.getToAccountNumber() != null && request.getToAccountNumber().equals(user.getAccountNumber())) {
				transaction.setStatus("FAILED");
				transactionRepository.save(transaction);
				throw new Exception("Cannot transfer to your own account");
			}
			Optional<User> toUserOpt = userRepository.findByAccountNumber(request.getToAccountNumber());
			if (toUserOpt.isEmpty()) {
				transaction.setStatus("FAILED");
				transactionRepository.save(transaction);
				throw new Exception("Recipient account not found");
			}
			User toUser = toUserOpt.get();
			if (!"approved".equalsIgnoreCase(toUser.getAccountStatus()) && !"active".equalsIgnoreCase(toUser.getAccountStatus())) {
				transaction.setStatus("FAILED");
				transactionRepository.save(transaction);
				throw new Exception("Recipient account is not active");
			}
			transaction.setStatus("SUCCESS");
			message = "Transfer successful";
			
			// Create transaction for recipient
			Transaction recipientTransaction = new Transaction();
			recipientTransaction.setUser(toUser);
			recipientTransaction.setTransactionType("DEPOSIT");
			recipientTransaction.setAmount(request.getAmount());
			recipientTransaction.setDescription("Transfer from account " + user.getAccountNumber());
			recipientTransaction.setTransactionDate(LocalDateTime.now());
			recipientTransaction.setStatus("SUCCESS");
			recipientTransaction.setToAccountNumber(user.getAccountNumber());
			transactionRepository.save(recipientTransaction);
		} else {
			throw new Exception("Invalid transaction type");
		}
		
		Transaction savedTransaction = transactionRepository.save(transaction);
		
		TransactionResponse response = new TransactionResponse();
		response.setTransactionId(savedTransaction.getTransactionId());
		response.setTransactionType(savedTransaction.getTransactionType());
		response.setAmount(savedTransaction.getAmount());
		response.setDescription(savedTransaction.getDescription());
		response.setTransactionDate(savedTransaction.getTransactionDate());
		response.setStatus(savedTransaction.getStatus());
		response.setToAccountNumber(savedTransaction.getToAccountNumber());
		response.setMessage(message);
		
		return response;
	}

	@Override
	public List<Transaction> getTransactionHistory(String username) {
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			return List.of();
		}
		return transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(account.getUser().getUserId());
	}

	@Override
	public User getUserByUsername(String username) {
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			return null;
		}
		return account.getUser();
	}
	
	@Override
	public double calculateBalance(Long userId) {
		return calculateCurrentBalance(userId);
	}
	
	private double calculateCurrentBalance(Long userId) {
		List<Transaction> transactions = transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(userId);
		double balance = 0.0;
		
		// Calculate balance purely from transactions
		// The initial deposit is already recorded as a DEPOSIT transaction when account is approved
		// So we don't need to add initialDepositAmount separately
		for (Transaction t : transactions) {
			if ("SUCCESS".equals(t.getStatus())) {
				if ("DEPOSIT".equals(t.getTransactionType())) {
					balance += t.getAmount();
				} else if ("WITHDRAWAL".equals(t.getTransactionType()) || "TRANSFER".equals(t.getTransactionType())) {
					balance -= t.getAmount();
				}
			}
		}
		
		return balance;
	}
}
