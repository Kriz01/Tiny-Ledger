package com.teya.ledger.repository;

import com.teya.ledger.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    List<Transaction> getAllTransactions(Long lastTransactionId, int limit, Long accountId);

    void insertTransaction(Long accountId, String type, Integer amount, UUID idempotencyKey);

    void updateStatus(UUID idempotencyKey, String status);

    Optional<String> getStatusByIdempotencyKey(UUID idempotencyKey);
}
