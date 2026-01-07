package com.tcs.dto;

import java.time.LocalDateTime;

public class TransactionResponse {
	private Long transactionId;
	private String transactionType;
	private Double amount;
	private String description;
	private LocalDateTime transactionDate;
	private String status;
	private Long toAccountNumber;
	private String message;
	
	public TransactionResponse() {
		super();
	}

	public TransactionResponse(Long transactionId, String transactionType, Double amount, String description,
			LocalDateTime transactionDate, String status, Long toAccountNumber, String message) {
		super();
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.amount = amount;
		this.description = description;
		this.transactionDate = transactionDate;
		this.status = status;
		this.toAccountNumber = toAccountNumber;
		this.message = message;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

