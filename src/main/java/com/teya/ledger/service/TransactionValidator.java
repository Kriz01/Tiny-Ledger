package com.teya.ledger.service;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.model.TransactionType;

public interface TransactionValidator {
    void validateAccount(Long accountId) throws AccountNotFoundException;

    void validateTransaction(Long accountId, Integer amount, TransactionType type);

}