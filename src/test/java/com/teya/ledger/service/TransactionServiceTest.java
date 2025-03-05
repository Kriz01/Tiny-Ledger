package com.teya.ledger.service;

import com.teya.ledger.BaseTest;
import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.exception.TransactionNotFoundException;
import com.teya.ledger.exception.TransactionProcessingException;
import com.teya.ledger.model.*;
import com.teya.ledger.repository.TransactionRepository;
import com.teya.ledger.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.teya.ledger.constants.Constants.TRANSACTION_ALREADY_COMPLETED_MSG;
import static com.teya.ledger.constants.Constants.TRANSACTION_COMPLETED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest extends BaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionValidator transactionValidator;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // Test case: Successful deposit
    @Test
    void testDepositAmountSuccess() {
        // Mock idempotency key check
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

        // Mock validation
        doNothing().when(transactionValidator).validateTransaction(TEST_ACCOUNT_ID, TEST_AMOUNT, TransactionType.DEPOSIT);

        // Mock transaction execution
        doNothing().when(transactionRepository).insertTransaction(TEST_ACCOUNT_ID, TransactionType.DEPOSIT.name(), TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);
        doNothing().when(accountService).updateBalance(TEST_ACCOUNT_ID, TransactionType.DEPOSIT.name(), TEST_AMOUNT);
        doNothing().when(transactionRepository).updateStatus(TEST_IDEMPOTENCY_KEY, Status.COMPLETED.name());

        // Execute
        ResponseEntity<TransactionMessageResponse> response = transactionService.depositAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(TRANSACTION_COMPLETED_MSG, Objects.requireNonNull(response.getBody()).message());
    }

    // Test case: Successful withdrawal
    @Test
    void testWithdrawAmountSuccess() {
        // Mock idempotency key check
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

        // Mock validation
        doNothing().when(transactionValidator).validateTransaction(TEST_ACCOUNT_ID, TEST_AMOUNT, TransactionType.WITHDRAWAL);

        // Mock transaction execution
        doNothing().when(transactionRepository).insertTransaction(TEST_ACCOUNT_ID, TransactionType.WITHDRAWAL.name(), TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);
        doNothing().when(accountService).updateBalance(TEST_ACCOUNT_ID, TransactionType.WITHDRAWAL.name(), TEST_AMOUNT);
        doNothing().when(transactionRepository).updateStatus(TEST_IDEMPOTENCY_KEY, Status.COMPLETED.name());

        // Execute
        ResponseEntity<TransactionMessageResponse> response = transactionService.withdrawAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(TRANSACTION_COMPLETED_MSG, Objects.requireNonNull(response.getBody()).message());
    }

    // Test case: Insufficient balance for withdrawal
    @Test
    void testWithdrawAmountInsufficientBalance() {
        // Mock idempotency key check
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

        // Mock transaction execution to throw InsufficientBalanceException
        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(transactionValidator).validateTransaction(TEST_ACCOUNT_ID, TEST_AMOUNT, TransactionType.WITHDRAWAL);

        // Execute and verify exception
        assertThrows(InsufficientBalanceException.class, () -> transactionService.withdrawAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY));
    }

    // Test case: Duplicate idempotency key (already completed)
    @Test
    void testTransactionWithDuplicateIdempotencyKey() {
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY)).thenReturn(Optional.of(Status.COMPLETED.name()));

        ResponseEntity<TransactionMessageResponse> response = transactionService.depositAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TRANSACTION_ALREADY_COMPLETED_MSG, Objects.requireNonNull(response.getBody()).message());
    }

    // Test case: Database error during transaction processing
    @Test
    void testTransactionProcessingException() {
        // Mock idempotency key check
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

        // Mock validation
        doNothing().when(transactionValidator).validateTransaction(TEST_ACCOUNT_ID, TEST_AMOUNT, TransactionType.DEPOSIT);

        // Mock transaction execution to throw an exception
        doThrow(new RuntimeException("Database connection error"))
                .when(transactionRepository).insertTransaction(TEST_ACCOUNT_ID, TransactionType.DEPOSIT.name(), TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        // Execute and verify exception
        assertThrows(TransactionProcessingException.class, () -> transactionService.depositAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY));
    }

    // Test case: Get all transactions
    @Test
    void testGetAllTransactions() {
        Long lastTransactionId = 0L;
        int limit = 2;

        // Mock transactions
        List<Transaction> mockTransactions = List.of(
                new Transaction(1L, TransactionType.DEPOSIT, 100, null,null),
                new Transaction(2L, TransactionType.WITHDRAWAL, 50, null,null)
        );
        when(transactionRepository.getAllTransactions(lastTransactionId, limit, TEST_ACCOUNT_ID)).thenReturn(mockTransactions);
        doNothing().when(transactionValidator).validateAccount(TEST_ACCOUNT_ID);
        // Execute
        TransactionResponse response = transactionService.getAllTransactions(lastTransactionId, limit, TEST_ACCOUNT_ID);

        // Verify
        assertEquals(mockTransactions.size(), response.transactions().size(), "Transaction list size should match");
        assertEquals(mockTransactions.getLast().id(), response.metadata().lastSeenId(), "Last transaction ID should match");
    }

    // Test case: No transactions found for account
    @Test
    void testGetAllTransactions_NoTransactions() {
        when(transactionRepository.getAllTransactions(null, 10, TEST_ACCOUNT_ID)).thenReturn(List.of());
        doNothing().when(transactionValidator).validateAccount(TEST_ACCOUNT_ID);
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getAllTransactions(null, 10, TEST_ACCOUNT_ID));
    }

    // Test case: Idempotency key in PENDING state
    @Test
    void testProcessTransaction_IdempotencyKeyPending() {
        // Mock idempotency key check
        when(transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY))
                .thenReturn(Optional.of(Status.PENDING.name()));

        // Execute
        ResponseEntity<TransactionMessageResponse> response = transactionService.depositAmount(TEST_ACCOUNT_ID, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        // Verify
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Transaction is still being processed.", Objects.requireNonNull(response.getBody()).message());
    }

    // Test case: Invalid account ID
    @Test
    void testValidateTransaction_InvalidAccount() {
        // Mock validation to throw AccountNotFoundException
        doThrow(new AccountNotFoundException("Account not found"))
                .when(transactionValidator).validateTransaction(999L, TEST_AMOUNT, TransactionType.DEPOSIT);

        assertThrows(AccountNotFoundException.class, () -> transactionService.depositAmount(999L, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY));
    }


}