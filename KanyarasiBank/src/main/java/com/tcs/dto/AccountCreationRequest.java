package com.tcs.dto;

import com.tcs.entity.User;

public class AccountCreationRequest {
	private User user;
	private String username;
	private String password;
	private String transactionPin;
	
	public AccountCreationRequest() {
		super();
	}

	public AccountCreationRequest(User user, String username, String password, String transactionPin) {
		super();
		this.user = user;
		this.username = username;
		this.password = password;
		this.transactionPin = transactionPin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTransactionPin() {
		return transactionPin;
	}

	public void setTransactionPin(String transactionPin) {
		this.transactionPin = transactionPin;
	}
}

