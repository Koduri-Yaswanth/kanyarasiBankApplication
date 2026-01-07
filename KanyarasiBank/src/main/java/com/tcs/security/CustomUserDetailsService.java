package com.tcs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tcs.entity.Account;
import com.tcs.entity.Admin;
import com.tcs.repository.AccountRepository;
import com.tcs.repository.AdminRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Try to find in Account (User)
		Account account = accountRepository.findByUsername(username);
		if (account != null) {
			return org.springframework.security.core.userdetails.User.builder()
					.username(account.getUsername())
					.password(account.getPassword())
					.roles(account.getRole().replace("ROLE_", ""))
					.build();
		}
		
		// Try to find in Admin
		Admin admin = adminRepository.findByUsername(username);
		if (admin != null) {
			return org.springframework.security.core.userdetails.User.builder()
					.username(admin.getUsername())
					.password(admin.getPassword())
					.roles(admin.getRole().replace("ROLE_", ""))
					.build();
		}
		
		throw new UsernameNotFoundException("User not found: " + username);
	}
}

