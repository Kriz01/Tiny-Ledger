package com.teya.ledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teya.ledger.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class BaseTest {

    protected static final Long TEST_ACCOUNT_ID = 123L;
    protected static final Long TEST_ACCOUNT_ID1 = 1L;
    protected static final Integer TEST_AMOUNT = 1000;
    protected static final Integer TEST_AMOUNT2 = 200;
    protected static final UUID TEST_IDEMPOTENCY_KEY = UUID.randomUUID();
    protected static final String SUCCESS_MSG = "Transaction completed successfully!";
    protected static final String INSUFFICIENT_BALANCE_MSG = "Insufficient balance for withdrawal";
    protected static final AmountRequest amountRequest = new AmountRequest(TEST_AMOUNT);
    protected static final Transaction transaction = new Transaction(1L, TransactionType.DEPOSIT, 9000, LocalDateTime.parse("2024-02-26 08:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.COMPLETED);
    public TransactionResponse createTransactionResponse(Long lastTransactionId, int limit) {
        var transactions = getExpectedTransactions(lastTransactionId, limit);
        return new TransactionResponse(transactions, new Metadata(transactions.size(), lastTransactionId));
    }


    // Helper method to generate expected transactions
    protected List<Transaction> getExpectedTransactions(Long lastTransactionId, int limit) {
        List<Transaction> transactions = new ArrayList<>();

        // Add transactions based on the limit and lastTransactionId
        if (lastTransactionId == null) {
            // If no lastTransactionId, return the first `limit` transactions

            transactions.addAll(Stream.of(
                    new Transaction(3L, TransactionType.WITHDRAWAL, 700, LocalDateTime.parse("2024-02-26 08:06:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.PENDING),
                    new Transaction(2L, TransactionType.WITHDRAWAL, 700, LocalDateTime.parse("2024-02-26 08:05:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.PENDING),
                    new Transaction(1L, TransactionType.DEPOSIT, 9000, LocalDateTime.parse("2024-02-26 08:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.COMPLETED)).limit(limit).toList());
        } else if (lastTransactionId == 3) {
            transactions.addAll(Stream.of(
                    new Transaction(2L, TransactionType.WITHDRAWAL, 700, LocalDateTime.parse("2024-02-26 08:05:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.PENDING),
                    new Transaction(1L, TransactionType.DEPOSIT, 9000, LocalDateTime.parse("2024-02-26 08:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.COMPLETED)).limit(limit).toList());
        } else if (lastTransactionId == 2) {
            transactions.add(
                    new Transaction(1L, TransactionType.DEPOSIT, 9000, LocalDateTime.parse("2024-02-26 08:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Status.COMPLETED));
        }

        return transactions;
    }
}
