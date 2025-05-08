package com.bank.springbootbank.config;

import com.bank.springbootbank.model.Admin;
import com.bank.springbootbank.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Override
    public void run(String... args) {
        // Create default admin account if it doesn't exist
        try {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword("admin");
            defaultAdmin.setEmail("admin@bank.com");
            defaultAdmin.setFullName("System Administrator");
            adminService.createInitialAdmin(defaultAdmin);
        } catch (Exception e) {
            // If admin already exists, ignore the error
            System.out.println("Default admin account already exists or could not be created.");
        }
    }
} 