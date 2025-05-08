package com.bank.springbootbank.service;

import com.bank.springbootbank.model.Admin;
import com.bank.springbootbank.model.User;
import com.bank.springbootbank.model.Account;
import com.bank.springbootbank.model.Transaction;
import com.bank.springbootbank.repository.AdminRepository;
import com.bank.springbootbank.repository.UserRepository;
import com.bank.springbootbank.repository.AccountRepository;
import com.bank.springbootbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Admin createInitialAdmin(Admin admin) {
        // Check if username already exists
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin createAdmin(Admin newAdmin, Admin creator) {
        // Verify that the creator is an existing admin
        if (creator == null || !adminRepository.existsById(creator.getId())) {
            throw new RuntimeException("Only existing admins can create new admin accounts");
        }

        // Check if username already exists
        if (adminRepository.existsByUsername(newAdmin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        return adminRepository.save(newAdmin);
    }

    public Optional<Admin> login(String username, String password) {
        return adminRepository.findByUsername(username)
                .filter(admin -> admin.getPassword().equals(password));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // First delete all accounts associated with the user
            List<Account> userAccounts = accountRepository.findByUser(user);
            for (Account account : userAccounts) {
                // Delete all transactions for this account
                List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId());
                for (Transaction transaction : transactions) {
                    transactionRepository.delete(transaction);
                }
                // Then delete the account
                accountRepository.delete(account);
            }
            // Finally delete the user
            userRepository.delete(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            // Delete all transactions for this account
            List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId());
            for (Transaction transaction : transactions) {
                transactionRepository.delete(transaction);
            }
            // Then delete the account
            accountRepository.delete(account);
        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }
} 