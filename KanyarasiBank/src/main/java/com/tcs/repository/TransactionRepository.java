package com.tcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByUser_UserIdOrderByTransactionDateDesc(Long userId);
	List<Transaction> findAllByOrderByTransactionDateDesc();
}

