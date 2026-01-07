package com.tcs.dto;

public class TransactionRequest {
	private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
	private Double amount;
	private String description;
	private String transactionPin;
	private Long toAccountNumber; // For transfer transactions
	
	public TransactionRequest() {
		super();
	}

	public TransactionRequest(String transactionType, Double amount, String description, String transactionPin,
			Long toAccountNumber) {
		super();
		this.transactionType = transactionType;
		this.amount = amount;
		this.description = description;
		this.transactionPin = transactionPin;
		this.toAccountNumber = toAccountNumber;
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

	public String getTransactionPin() {
		return transactionPin;
	}

	public void setTransactionPin(String transactionPin) {
		this.transactionPin = transactionPin;
	}

	public Long getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(Long toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}
}

