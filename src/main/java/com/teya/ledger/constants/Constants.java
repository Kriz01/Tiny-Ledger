package com.teya.ledger.constants;

public class Constants {
    public static final String ACCOUNT_ID = "accountId";

    public static final String HISTORY_PATH = "/history";
    public static final String ACCOUNT_ID_PATH = "/account/" + "{" + ACCOUNT_ID + "}";
    public static final String DEPOSIT_PATH = "/deposit" + ACCOUNT_ID_PATH;
    public static final String WITHDRAW_PATH = "/withdraw" + ACCOUNT_ID_PATH;
    public static final String TRANSACTION_PATH = "/transactions";
    public static final String BALANCE_PATH = "/balance";

    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    public static final String TRANSACTION_COMPLETED_MSG = "Transaction completed successfully.";
    public static final String WITHDRAWAL_SUCCESS_MSG = "Withdrawal successful.";
    public static final String INSUFFICIENT_BALANCE_MSG = "Insufficient balance for withdrawal.";
    public static final String TRANSACTION_ALREADY_COMPLETED_MSG = "Transaction already completed.";
    public static final String TRANSACTION_PENDING_MSG = "Transaction is still being processed.";
    public static final String TRANSACTION_FAILED_MSG = "Transaction failed previously. Please try again.";
    public static final String INVALID_TRANSACTION_STATUS_MSG = "Invalid transaction status.";
     public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String TRANSACTION_NOT_FOUND = "No transactions found for account ID: ";
    public static final String TRANSACTION_PROCESSING_FAILED = "Failed to process transaction: ";
    public static final String INCORRECT_AMOUNT = "Amount must be greater than zero.";

    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String TIMESTAMP = "timestamp";
    public static final String LIMIT = "limit";
    public static final String ID = "id";
    public static final String LAST_TRANSACTION_ID = "lastTransactionId";
    public static final String IDEMPOTENCY_KEY = "idempotencyKey";
    public static final String STATUS = "status";

    private Constants() {
    }
}
