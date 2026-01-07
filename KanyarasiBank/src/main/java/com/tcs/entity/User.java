package com.tcs.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_user")
@JsonIgnoreProperties(value = {"isCreatedAt", "isUpdatedAt", "isDeleated"}, allowGetters = false, allowSetters = true)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	
	@Column(nullable = false)
	private String fullName;
	private String gender;

	@Column(nullable = false)
	private LocalDate dob;

	@Column(nullable = false)
	private String nationality;   //indian, other

	@Column(nullable = false, length = 10)
	private double mobileNumber;
	private String email;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, length = 12)
	private Long aadhar;

	@Column(nullable = false, length = 10)
	private String pan;

	@Column(nullable = false)
	private String accountType;   //savings, current, salary, student
	
	private double initialDepositAmount;   //minimum 1000
	
	//need to add aadhar photo verification.
	
	//generated automatically
	@Column(length = 12)
	private long accountNumber;
	private String branchName;
	private String ifscCode;
	private String accountStatus;   //set automatically after creation. active, pending, blocked, closed, approved, rejected
	private LocalDateTime isCreatedAt;
	private LocalDateTime isUpdatedAt;
	private boolean isDeleated;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String fullName, String gender, LocalDate dob, String nationality, double mobileNumber, String email,
			String address, Long aadhar, String pan, String accountType, double initialDepositAmount, long accountNumber,
			String branchName, String ifscCode, String accountStatus, LocalDateTime isCreatedAt, LocalDateTime isUpdatedAt,
			boolean isDeleated) {
		super();
		this.fullName = fullName;
		this.gender = gender;
		this.dob = dob;
		this.nationality = nationality;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.address = address;
		this.aadhar = aadhar;
		this.pan = pan;
		this.accountType = accountType;
		this.initialDepositAmount = initialDepositAmount;
		this.accountNumber = accountNumber;
		this.branchName = branchName;
		this.ifscCode = ifscCode;
		this.accountStatus = accountStatus;
		this.isCreatedAt = isCreatedAt;
		this.isUpdatedAt = isUpdatedAt;
		this.isDeleated = isDeleated;
	}

	public User(String fullName, String gender, LocalDate dob, String nationality, double mobileNumber, String email,
			String address, Long aadhar, String pan, String accountType, double initialDepositAmount) {
		super();
		this.fullName = fullName;
		this.gender = gender;
		this.dob = dob;
		this.nationality = nationality;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.address = address;
		this.aadhar = aadhar;
		this.pan = pan;
		this.accountType = accountType;
		this.initialDepositAmount = initialDepositAmount;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public double getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(double mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getAadhar() {
		return aadhar;
	}

	public void setAadhar(Long aadhar) {
		this.aadhar = aadhar;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getInitialDepositAmount() {
		return initialDepositAmount;
	}

	public void setInitialDepositAmount(double initialDepositAmount) {
		this.initialDepositAmount = initialDepositAmount;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public LocalDateTime isCreatedAt() {
		return isCreatedAt;
	}

	public void setCreatedAt(LocalDateTime isCreatedAt) {
		this.isCreatedAt = isCreatedAt;
	}

	public LocalDateTime isUpdatedAt() {
		return isUpdatedAt;
	}

	public void setUpdatedAt(LocalDateTime isUpdatedAt) {
		this.isUpdatedAt = isUpdatedAt;
	}

	public boolean isDeleated() {
		return isDeleated;
	}

	public void setDeleated(boolean isDeleated) {
		this.isDeleated = isDeleated;
	}
	
	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
}
