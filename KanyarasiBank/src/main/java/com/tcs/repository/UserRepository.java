package com.tcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	public User findByAadhar(Long aadhar);
	public List<User> findByAccountStatus(String accountStatus);
	public List<User> findByIsDeleatedFalse();
	public Optional<User> findByAccountNumber(Long accountNumber);
}
