package com.tcs.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = DuplicateUserException.class)
	public String handleException(DuplicateUserException e) {
		return e.getMessage();
	}

	@ExceptionHandler(value = LowAmountException.class)
	public String handleException(LowAmountException e) {
		return e.getMessage();
	}
	
	@ExceptionHandler(value = InvalidAccountType.class)
	public String handleException(InvalidAccountType e) {
		return e.getMessage();
	}
}
