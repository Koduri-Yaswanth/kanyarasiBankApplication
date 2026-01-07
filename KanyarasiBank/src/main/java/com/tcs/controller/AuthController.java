package com.tcs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.dto.LoginRequest;
import com.tcs.entity.Account;
import com.tcs.repository.AccountRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private AccountRepository accountRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			// Check account status before authentication for rejected accounts
			Account account = accountRepository.findByUsername(loginRequest.getUsername());
			if (account != null && account.getUser() != null) {
				String accountStatus = account.getUser().getAccountStatus();
				if ("rejected".equalsIgnoreCase(accountStatus)) {
					return ResponseEntity.badRequest().body("ACCOUNT_REJECTED: Your account request has been rejected. Please contact your nearest Kanyarasi bank branch.");
				}
			}
			
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Get user roles
			String role = authentication.getAuthorities().stream()
					.findFirst()
					.map(auth -> auth.getAuthority())
					.orElse("ROLE_USER");
			
			// Remove ROLE_ prefix if present for consistency
			if (role.startsWith("ROLE_")) {
				role = role.substring(5);
			}
			
			// Return login response with role
			java.util.Map<String, String> response = new java.util.HashMap<>();
			response.put("message", "Login successful");
			response.put("username", loginRequest.getUsername());
			response.put("role", role);
			
			return ResponseEntity.ok().body(response);
		} catch (org.springframework.security.authentication.BadCredentialsException e) {
			return ResponseEntity.badRequest().body("Invalid username or password");
		} catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
			return ResponseEntity.badRequest().body("User not found: " + loginRequest.getUsername());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		SecurityContextHolder.clearContext();
		return ResponseEntity.ok().body("Logout successful");
	}
}

