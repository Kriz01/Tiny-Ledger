package com.teya.ledger.service.impl;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.repository.AccountRepository;
import com.teya.ledger.service.AccountService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves the current balance of the specified account.
     * <p>
     * The balance is returned in <b>minor units</b> (e.g., cents for USD) to avoid floating-point precision issues
     * that can arise when dealing with monetary values. Using integers ensures accurate calculations and avoids
     * rounding errors that are common with floating-point arithmetic.
     * </p>
     *
     * @param accountId The ID of the account whose balance is to be retrieved.
     * @return The balance of the account in minor units.
     * @throws AccountNotFoundException If no account is found with the specified ID.
     */
    @Override
    public Integer getBalance(Long accountId) {
        return accountRepository.getBalance(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
    }

    /**
     * Updates the balance of a specified account based on the transaction type.
     * <p>
     * This method modifies the account balance by either adding or subtracting the given amount,
     * depending on whether the transaction type is a deposit or withdrawal.
     * </p>
     *
     * @param accountId The ID of the account whose balance is to be updated.
     * @param type      The type of transaction ("DEPOSIT" or "WITHDRAWAL").
     * @param amount    The amount to be added or deducted in minor units.
     * @throws AccountNotFoundException If no account is found with the specified ID.
     * @throws IllegalArgumentException If the transaction type is invalid or the amount is non-positive.
     */
    @Override
    public void updateBalance(Long accountId, String type, Integer amount) {
        accountRepository.updateBalance(accountId, type, amount);
    }

    /**
     * Checks whether an account exists in the system.
     * <p>
     * This method verifies if an account with the given ID is present in the repository.
     * It is useful for validating transactions and preventing operations on non-existent accounts.
     * </p>
     *
     * @param accountId The ID of the account to check.
     * @return {@code true} if the account exists, {@code false} otherwise.
     */
    @Override
    public boolean accountExists(Long accountId) {
        return accountRepository.accountExists(accountId);
    }

}
