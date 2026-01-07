package com.tcs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
	
	@Column(nullable = false)
	private Double amount;
	
	private String description;
	
	@Column(nullable = false)
	private LocalDateTime transactionDate;
	
	private String status; // SUCCESS, FAILED, PENDING
	
	private Long toAccountNumber; // For transfer transactions
	
	public Transaction() {
		super();
	}

	public Transaction(User user, String transactionType, Double amount, String description,
			LocalDateTime transactionDate, String status, Long toAccountNumber) {
		super();
		this.user = user;
		this.transactionType = transactionType;
		this.amount = amount;
		this.description = description;
		this.transactionDate = transactionDate;
		this.status = status;
		this.toAccountNumber = toAccountNumber;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(Long toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}
}
