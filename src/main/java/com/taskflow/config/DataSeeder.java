package com.taskflow.config;

import com.taskflow.model.AppUser;
import com.taskflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.security.crypto.password.PasswordEncoder; 

@Configuration
public class DataSeeder {
    
    @Bean 
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new AppUser("admin", encoder.encode("adminpass"), "ADMIN"));
                userRepository.save(new AppUser("alice", encoder.encode("alicepass"), "USER"));
            }
        };
    }
}
