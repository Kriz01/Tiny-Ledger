package com.teya.ledger.service.impl;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.service.AccountService;
import com.teya.ledger.service.TransactionValidator;
import org.springframework.stereotype.Component;

import static com.teya.ledger.constants.Constants.*;

@Component
public class TransactionValidatorImpl implements TransactionValidator {

    private final AccountService accountService;

    public TransactionValidatorImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Validates whether the account has sufficient balance for a withdrawal transaction.
     *
     * @param accountId ID of the account.
     * @param amount    Amount to be withdrawn.
     * @throws InsufficientBalanceException if the account balance is lower than the withdrawal amount.
     */
    public void validateBalance(Long accountId, Integer amount) throws InsufficientBalanceException {
        Integer currentBalance = accountService.getBalance(accountId);
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_MSG);
        }
    }

    /**
     * Checks whether the account exists in the system.
     *
     * @param accountId ID of the account.
     * @throws AccountNotFoundException if the account does not exist.
     */
    @Override
    public void validateAccount(Long accountId) throws AccountNotFoundException {
        if (!accountService.accountExists(accountId)) {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * Validates a transaction based on type (deposit or withdrawal).
     *
     * @param accountId ID of the account.
     * @param amount    Transaction amount.
     * @param type      Type of transaction (WITHDRAWAL or DEPOSIT).
     * @throws IllegalArgumentException     if the amount is invalid.
     * @throws AccountNotFoundException     if the account does not exist.
     * @throws InsufficientBalanceException if the account has insufficient funds for a withdrawal.
     */
    @Override
    public void validateTransaction(Long accountId, Integer amount, TransactionType type) {
        validateAmount(amount);
        validateAccount(accountId);
        if (type.equals(TransactionType.WITHDRAWAL)) {
            validateBalance(accountId, amount);
        }
    }

    /**
     * Ensures that the transaction amount is a positive value.
     *
     * @param amount Transaction amount.
     * @throws IllegalArgumentException if the amount is null or non-positive.
     */
    public void validateAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException(INCORRECT_AMOUNT);
        }
    }
}