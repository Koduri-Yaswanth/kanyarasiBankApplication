package com.tcs.dto;

import com.tcs.entity.User;

public class UserWithBalance {
	private User user;
	private double currentBalance;
	
	public UserWithBalance() {
		super();
	}
	
	public UserWithBalance(User user, double currentBalance) {
		super();
		this.user = user;
		this.currentBalance = currentBalance;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public double getCurrentBalance() {
		return currentBalance;
	}
	
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
}

