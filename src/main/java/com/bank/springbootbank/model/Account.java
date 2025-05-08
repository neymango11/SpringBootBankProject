package com.bank.springbootbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    private String accountType; // SAVINGS, CHECKING

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(nullable = true)
    private BigDecimal apy; // Annual Percentage Yield for savings accounts

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_interest_calculation")
    private LocalDateTime lastInterestCalculation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastInterestCalculation = LocalDateTime.now();
        if (apy == null) {
            apy = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Explicit getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountHolderName(String fullName) {
        this.accountHolderName = fullName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getApy() {
        return apy != null ? apy : BigDecimal.ZERO;
    }
} 