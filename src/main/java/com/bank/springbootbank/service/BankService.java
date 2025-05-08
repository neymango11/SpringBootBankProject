package com.bank.springbootbank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.springbootbank.model.Account;
import com.bank.springbootbank.model.Transaction;
import com.bank.springbootbank.model.User;
import com.bank.springbootbank.repository.AccountRepository;
import com.bank.springbootbank.repository.TransactionRepository;
import com.bank.springbootbank.repository.UserRepository;

@Service
public class BankService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String username, String password, String fullName, 
                           String address, String phoneNumber, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // In production, use proper password hashing
        user.setFullName(fullName);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAdmin(false);

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)) // In production, use proper password comparison
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }

    @Transactional
    public Account createAccount(User user, int accountTypeChoice) {
        String accountType;
        switch (accountTypeChoice) {
            case 1 -> accountType = "CHECKING";
            case 2 -> accountType = "SAVINGS";
            default -> throw new IllegalArgumentException("Invalid account type choice");
        }

        Account account = new Account();
        account.setUser(user);
        account.setAccountHolderName(user.getFullName());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateAccountNumber());
        return accountRepository.save(account);
    }

    @Transactional
    public List<Account> createBothAccounts(User user) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(createAccount(user, 1)); // Create checking account
        accounts.add(createAccount(user, 2)); // Create savings account
        return accounts;
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccountByNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        
        // Set APY for savings accounts based on initial deposit
        if (account.getAccountType().equals("SAVINGS") && account.getBalance().equals(amount)) {
            // APY tiers based on initial deposit
            if (amount.compareTo(new BigDecimal("10000")) >= 0) {
                account.setApy(new BigDecimal("0.05")); // 5% APY for deposits >= $10,000
            } else if (amount.compareTo(new BigDecimal("5000")) >= 0) {
                account.setApy(new BigDecimal("0.04")); // 4% APY for deposits >= $5,000
            } else if (amount.compareTo(new BigDecimal("1000")) >= 0) {
                account.setApy(new BigDecimal("0.03")); // 3% APY for deposits >= $1,000
            } else {
                account.setApy(new BigDecimal("0.02")); // 2% APY for deposits < $1,000
            }
            System.out.println("\nAPY set to " + account.getApy().multiply(new BigDecimal("100")) + "% based on initial deposit");
        }
        
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccountByNumber(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAWAL");
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account fromAccount = getAccountByNumber(fromAccountNumber);
        Account toAccount = getAccountByNumber(toAccountNumber);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Withdraw from source account
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        // Deposit to target account
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        // Create transfer transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType("TRANSFER");
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId());
    }

    public Account getAccount(String accountNumber) {
        return getAccountByNumber(accountNumber);
    }

    public List<Account> getUserAccounts(User user) {
        return accountRepository.findByUserId(user.getId());
    }

    private Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private String generateAccountNumber() {
        // Simple implementation - in production, use a more robust method
        return String.format("%010d", System.nanoTime() % 10000000000L);
    }
} 