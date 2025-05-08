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

/**
 * Stores all important details about a user's account
 * Like account number, type (checking or savings), balance, and owner
 * Each account has a unique ID and account number
 *
 *
 */

@Entity
@Table(name = "accounts")
@Getter // automatically creates getter and setter methods for all the fields in your class
@Setter // using lomboks library
public class Account {
    @Id // every table needs a primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID goes up by 1 (per user)
    private Long id; // field for ID

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber; // field for account number

    @Column(name = "account_type", nullable = false)
    private String accountType; // field for  accountType (SAVINGS OR CHECKINGS)

    @Column(nullable = false)
    private BigDecimal balance; // field for balance

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(nullable = true)
    private BigDecimal apy; // annual Percentage Yield for savings accounts

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // sets the account to the local date and time

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // sets the account to the local date and time

    @Column(name = "last_interest_calculation")
    private LocalDateTime lastInterestCalculation; // sets the last interest calculation with the most update date/time

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // FIGURE THIS OUT!!!!!!!!!!!!!!
    private User user;  


    /**
     * @PrePersist: method automatically runs right before creating an account for the first time
     * Features:
     * Sets time when created
     * When it's been updated which is now (just created)
     * lastInterestCalculation tracks when interest was last calculated
     */

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastInterestCalculation = LocalDateTime.now();
        if (apy == null) {
            apy = BigDecimal.ZERO;
        }
    }

    /**
     * @PreUpdate: method runs automatically right before an existing account is updated in the database
     * Features:
     * It updates the "updateAt" timestamp to the current time, so we know when the last time this account was changed
     */
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

    //makes sure that if apy is null, it returns 0 instead with lomboks deafult version it would just return null
    public BigDecimal getApy() {
        return apy != null ? apy : BigDecimal.ZERO;
    }
} 