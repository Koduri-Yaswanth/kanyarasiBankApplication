package com.tcs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tcs.entity.Admin;
import com.tcs.repository.AdminRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		// Create default admin if it doesn't exist
		if (!adminRepository.existsByUsername("admin")) {
			Admin admin = new Admin();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRole("ROLE_ADMIN");
			admin.setFullName("System Administrator");
			admin.setEmail("admin@kanyarasibank.com");
			adminRepository.save(admin);
			System.out.println("Default admin user created: username=admin, password=admin123");
		}
	}
}

