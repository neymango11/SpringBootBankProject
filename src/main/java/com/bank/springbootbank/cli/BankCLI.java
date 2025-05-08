package com.bank.springbootbank.cli;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.bank.springbootbank.model.Account;
import com.bank.springbootbank.model.Admin;
import com.bank.springbootbank.model.Transaction;
import com.bank.springbootbank.model.User;
import com.bank.springbootbank.service.AdminService;
import com.bank.springbootbank.service.BankService;
import com.bank.springbootbank.service.UserService;

@Component
public class BankCLI implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private AdminService adminService;

    private Scanner scanner = new Scanner(System.in);
    private User currentUser;
    private Admin currentAdmin;

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("\n=== Welcome to Spring Boot Bank ===");
            System.out.println("1. User Login");
            System.out.println("2. Admin Login");
            System.out.println("3. Register");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    adminLogin();
                    break;
                case 3:
                    register();
                    break;
                case 4:
                    System.out.println("Thank you for using Spring Boot Bank!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        Optional<Admin> admin = adminService.login(username, password);
        if (admin.isPresent()) {
            currentAdmin = admin.get();
            System.out.println("Welcome, " + currentAdmin.getFullName() + "!");
            displayAdminMenu();
        } else {
            System.out.println("Invalid admin credentials!");
        }
    }

    private void displayAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View All Users");
            System.out.println("2. View All Accounts");
            System.out.println("3. View User Details");
            System.out.println("4. View Account Details");
            System.out.println("5. Delete User");
            System.out.println("6. Delete Account");
            System.out.println("7. Create Admin Account");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    viewAllAccounts();
                    break;
                case 3:
                    viewUserDetails();
                    break;
                case 4:
                    viewAccountDetails();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    deleteAccount();
                    break;
                case 7:
                    createAdminAccount();
                    break;
                case 8:
                    currentAdmin = null;
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = adminService.getAllUsers();
        System.out.println("\n=== All Users ===");
        for (User user : users) {
            System.out.println("ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Full Name: " + user.getFullName());
            System.out.println("-------------------");
        }
    }

    private void viewAllAccounts() {
        List<Account> accounts = adminService.getAllAccounts();
        System.out.println("\n=== All Accounts ===");
        for (Account account : accounts) {
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Type: " + account.getAccountType());
            System.out.println("Balance: $" + account.getBalance());
            System.out.println("APY: " + account.getApy() + "%");
            System.out.println("-------------------");
        }
    }

    private void viewUserDetails() {
        System.out.print("Enter user ID: ");
        Long userId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Optional<User> user = adminService.getUserById(userId);
        if (user.isPresent()) {
            User foundUser = user.get();
            System.out.println("\n=== User Details ===");
            System.out.println("ID: " + foundUser.getId());
            System.out.println("Username: " + foundUser.getUsername());
            System.out.println("Email: " + foundUser.getEmail());
            System.out.println("Full Name: " + foundUser.getFullName());
        } else {
            System.out.println("User not found!");
        }
    }

    private void viewAccountDetails() {
        System.out.print("Enter account ID: ");
        Long accountId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Optional<Account> account = adminService.getAccountById(accountId);
        if (account.isPresent()) {
            Account foundAccount = account.get();
            System.out.println("\n=== Account Details ===");
            System.out.println("Account Number: " + foundAccount.getAccountNumber());
            System.out.println("Type: " + foundAccount.getAccountType());
            System.out.println("Balance: $" + foundAccount.getBalance());
            System.out.println("APY: " + foundAccount.getApy() + "%");
            
            // Get the user's other account
            User accountHolder = foundAccount.getUser();
            List<Account> allAccounts = bankService.getUserAccounts(accountHolder);
            
            if (allAccounts.size() > 1) {
                System.out.println("\n=== User's Other Account ===");
                for (Account otherAccount : allAccounts) {
                    if (!otherAccount.getAccountNumber().equals(foundAccount.getAccountNumber())) {
                        System.out.println("Account Number: " + otherAccount.getAccountNumber());
                        System.out.println("Type: " + otherAccount.getAccountType());
                        System.out.println("Balance: $" + otherAccount.getBalance());
                        System.out.println("APY: " + otherAccount.getApy() + "%");
                    }
                }
            }
        } else {
            System.out.println("Account not found!");
        }
    }

    private void deleteUser() {
        System.out.print("Enter user ID to delete: ");
        Long userId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        try {
            adminService.deleteUser(userId);
            System.out.println("User deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    private void deleteAccount() {
        System.out.print("Enter account ID to delete: ");
        Long accountId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        try {
            adminService.deleteAccount(accountId);
            System.out.println("Account deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    private void register() {
        System.out.println("\n=== Register New Account ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        User user = bankService.registerUser(username, password, fullName, address, phoneNumber, email);
        System.out.println("Registration successful!");
        currentUser = user;

        // Directly show account type selection
        System.out.println("\nSelect account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.println("3. Both Accounts");
        System.out.print("Enter your choice (1-3): ");
        
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1" -> {
                    Account account = bankService.createAccount(currentUser, 2); // 2 for savings
                    System.out.println("\nSavings account created successfully!");
                    System.out.println("Account Number: " + account.getAccountNumber());
                    
                    // Prompt for initial deposit for savings account
                    System.out.print("\nWould you like to make an initial deposit to your savings account? (yes/no): ");
                    String depositChoice = scanner.nextLine().toLowerCase();
                    if (depositChoice.equals("yes") || depositChoice.equals("y")) {
                        System.out.print("Enter initial deposit amount: $");
                        String amountStr = scanner.nextLine();
                        try {
                            BigDecimal amount = new BigDecimal(amountStr);
                            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                System.out.println("Error: Amount must be greater than zero");
                            } else {
                                bankService.deposit(account.getAccountNumber(), amount);
                                System.out.println("Initial deposit successful!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid amount format");
                        }
                    }
                }
                case "2" -> {
                    Account account = bankService.createAccount(currentUser, 1); // 1 for checking
                    System.out.println("\nChecking account created successfully!");
                    System.out.println("Account Number: " + account.getAccountNumber());
                    
                    // Prompt for initial deposit for checking account
                    System.out.print("\nWould you like to make an initial deposit to your checking account? (yes/no): ");
                    String depositChoice = scanner.nextLine().toLowerCase();
                    if (depositChoice.equals("yes") || depositChoice.equals("y")) {
                        System.out.print("Enter initial deposit amount: $");
                        String amountStr = scanner.nextLine();
                        try {
                            BigDecimal amount = new BigDecimal(amountStr);
                            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                System.out.println("Error: Amount must be greater than zero");
                            } else {
                                bankService.deposit(account.getAccountNumber(), amount);
                                System.out.println("Initial deposit successful!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid amount format");
                        }
                    }
                }
                case "3" -> {
                    List<Account> accounts = bankService.createBothAccounts(currentUser);
                    System.out.println("\nBoth accounts created successfully!");
                    for (Account account : accounts) {
                        System.out.println(account.getAccountType() + " Account Number: " + account.getAccountNumber());
                    }
                    
                    // Prompt for initial deposits for both accounts
                    for (Account account : accounts) {
                        System.out.print("\nWould you like to make an initial deposit to your " + 
                            account.getAccountType().toLowerCase() + " account? (yes/no): ");
                        String depositChoice = scanner.nextLine().toLowerCase();
                        if (depositChoice.equals("yes") || depositChoice.equals("y")) {
                            System.out.print("Enter initial deposit amount: $");
                            String amountStr = scanner.nextLine();
                            try {
                                BigDecimal amount = new BigDecimal(amountStr);
                                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                    System.out.println("Error: Amount must be greater than zero");
                                } else {
                                    bankService.deposit(account.getAccountNumber(), amount);
                                    System.out.println("Initial deposit successful!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid amount format");
                            }
                        }
                    }
                }
                default -> System.out.println("\nInvalid choice. Please enter 1, 2, or 3.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            currentUser = bankService.login(username, password);
            System.out.println("Login successful!");
            displayUserMenu();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayUserMenu() {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. View Transaction History");
            System.out.println("5. Transfer Money");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    deposit();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    checkBalance();
                    break;
                case 4:
                    viewTransactionHistory();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }

    private Account selectAccount() {
        List<Account> accounts = bankService.getUserAccounts(currentUser);
        if (accounts.isEmpty()) {
            throw new RuntimeException("No accounts found");
        }

        System.out.println("\nSelect account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.print("Enter your choice (1-2): ");
        
        String choice = scanner.nextLine();
        return accounts.stream()
                .filter(account -> (choice.equals("1") && account.getAccountType().equals("SAVINGS")) ||
                                 (choice.equals("2") && account.getAccountType().equals("CHECKING")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid account type selection"));
    }

    private void deposit() {
        try {
            Account account = selectAccount();
            System.out.print("Enter amount to deposit: $");
            String amountStr = scanner.nextLine();
            if (amountStr.trim().isEmpty()) {
                System.out.println("Error: Amount cannot be empty");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Error: Amount must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid amount format");
                return;
            }
            
            Transaction transaction = bankService.deposit(account.getAccountNumber(), amount);
            System.out.println("Deposit successful!");
            System.out.println("New balance: " + transaction.getAccount().getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw() {
        try {
            Account account = selectAccount();
            System.out.print("Enter amount to withdraw: $");
            String amountStr = scanner.nextLine();
            if (amountStr.trim().isEmpty()) {
                System.out.println("Error: Amount cannot be empty");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Error: Amount must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid amount format");
                return;
            }
            
            Transaction transaction = bankService.withdraw(account.getAccountNumber(), amount);
            System.out.println("Withdrawal successful!");
            System.out.println("New balance: " + transaction.getAccount().getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkBalance() {
        try {
            Account account = selectAccount();
            System.out.println("\nAccount Details:");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Account Holder: " + account.getAccountHolderName());
            System.out.println("Current Balance: " + account.getBalance());
            if (account.getAccountType().equals("SAVINGS")) {
                System.out.println("APY: " + account.getApy().multiply(new BigDecimal("100")) + "%");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        try {
            Account account = selectAccount();
            System.out.println("\nTransaction History for " + account.getAccountType() + " Account: " + account.getAccountNumber());
            System.out.println("Account Holder: " + account.getAccountHolderName());
            List<Transaction> transactions = bankService.getTransactionHistory(account.getAccountNumber());
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                System.out.println("\nDate\t\t\tType\t\tAmount");
                System.out.println("------------------------------------------------");
                for (Transaction transaction : transactions) {
                    System.out.printf("%s\t%s\t\t%s%n",
                            transaction.getTransactionDate(),
                            transaction.getTransactionType(),
                            transaction.getAmount());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transfer() {
        try {
            System.out.println("\n=== Transfer Money ===");
            
            // Select source account
            System.out.println("Select source account:");
            Account fromAccount = selectAccount();
            
            // Get recipient's account number
            System.out.print("Enter recipient's account number: ");
            String toAccountNumber = scanner.nextLine();
            
            // Enter amount
            System.out.print("Enter amount to transfer: $");
            String amountStr = scanner.nextLine();
            if (amountStr.trim().isEmpty()) {
                System.out.println("Error: Amount cannot be empty");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Error: Amount must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid amount format");
                return;
            }
            
            Transaction transaction = bankService.transfer(fromAccount.getAccountNumber(), toAccountNumber, amount);
            System.out.println("Transfer successful!");
            System.out.println("New balance: " + transaction.getAccount().getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createAdminAccount() {
        System.out.println("\n=== Create Admin Account ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        try {
            Admin newAdmin = new Admin();
            newAdmin.setUsername(username);
            newAdmin.setPassword(password);
            newAdmin.setEmail(email);
            newAdmin.setFullName(fullName);

            Admin createdAdmin = adminService.createAdmin(newAdmin, currentAdmin);
            System.out.println("Admin account created successfully!");
            System.out.println("Admin ID: " + createdAdmin.getId());
            System.out.println("Username: " + createdAdmin.getUsername());
            System.out.println("Full Name: " + createdAdmin.getFullName());
        } catch (Exception e) {
            System.out.println("Error creating admin account: " + e.getMessage());
        }
    }
} 