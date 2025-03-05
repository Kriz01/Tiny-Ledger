package com.teya.ledger.utils;

public class SqlQueries {

    // AccountRepository SQL Queries
    public static final String GET_BALANCE = "SELECT balance FROM accounts WHERE account_id = :accountId";
    public static final String UPDATE_BALANCE_DEPOSIT = "UPDATE accounts SET balance = balance + :amount WHERE account_id = :accountId";
    public static final String UPDATE_BALANCE_WITHDRAWAL = "UPDATE accounts SET balance = balance - :amount WHERE account_id = :accountId";
    public static final String ACCOUNT_EXISTS = "SELECT EXISTS(SELECT 1 FROM accounts WHERE account_id = :accountId)";

    // TransactionRepository SQL Queries
    public static final String GET_ALL_TRANSACTIONS_WITHOUT_LAST_ID = "SELECT * FROM transactions WHERE account_id = :accountId ORDER BY id DESC LIMIT :limit";
    public static final String GET_ALL_TRANSACTIONS_WITH_LAST_ID = "SELECT * FROM transactions WHERE id < :lastTransactionId AND account_id = :accountId ORDER BY id DESC LIMIT :limit";
    public static final String INSERT_TRANSACTION = "INSERT INTO transactions (account_id, type, amount, timestamp, idempotency_key, status) " +
            "VALUES (:accountId, :type, :amount, CURRENT_TIMESTAMP, :idempotencyKey, 'PENDING')";
    public static final String UPDATE_TRANSACTION_STATUS = "UPDATE transactions SET status = :status WHERE idempotency_key = :idempotencyKey";
    public static final String GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY = "SELECT status FROM transactions WHERE idempotency_key = :idempotencyKey";
}
