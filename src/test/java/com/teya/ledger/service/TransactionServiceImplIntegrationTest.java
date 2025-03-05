package com.teya.ledger.service;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.model.*;
import com.teya.ledger.repository.TransactionRepository;
import com.teya.ledger.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static com.teya.ledger.constants.Constants.TRANSACTION_COMPLETED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
public class TransactionServiceImplIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionValidator transactionValidator;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setup() {
        transactionService = new TransactionServiceImpl(
                transactionRepository,
                accountService,
                transactionValidator
        );
    }

    @ParameterizedTest
    @MethodSource("provideTransactionParameters")
    public void testProcessTransaction(TransactionType type, Integer amount, Integer expectedBalance) {
        UUID idempotencyKey = UUID.randomUUID();
        ResponseEntity<TransactionMessageResponse> response;

        if (type == TransactionType.DEPOSIT) {
            response = transactionService.depositAmount(1L, amount, idempotencyKey);
        } else {
            response = transactionService.withdrawAmount(1L, amount, idempotencyKey);
        }

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(TRANSACTION_COMPLETED_MSG, Objects.requireNonNull(response.getBody()).message());

        Integer balance = accountService.getBalance(1L);
        assertEquals(expectedBalance, balance);
    }

    private static Stream<Arguments> provideTransactionParameters() {
        return Stream.of(
                Arguments.of(TransactionType.DEPOSIT, 100, 900),
                Arguments.of(TransactionType.WITHDRAWAL, 50, 750)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountParameters")
    public void testProcessTransaction_InvalidAmount(Integer amount) {
        UUID idempotencyKey = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> transactionService.depositAmount(1L, amount, idempotencyKey));
    }

    private static Stream<Arguments> provideInvalidAmountParameters() {
        return Stream.of(
                Arguments.of(-100),
                Arguments.of(0)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInsufficientBalanceParameters")
    public void testWithdrawAmount_InsufficientBalance(Integer amount) {
        UUID idempotencyKey = UUID.randomUUID();
        assertThrows(InsufficientBalanceException.class, () -> transactionService.withdrawAmount(1L, amount, idempotencyKey));
    }

    private static Stream<Arguments> provideInsufficientBalanceParameters() {
        return Stream.of(
                Arguments.of(900),
                Arguments.of(801)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAccountParameters")
    public void testProcessTransaction_AccountNotFound(Long accountId) {
        UUID idempotencyKey = UUID.randomUUID();
        assertThrows(AccountNotFoundException.class, () -> transactionService.withdrawAmount(accountId, 50, idempotencyKey));
    }

    private static Stream<Arguments> provideInvalidAccountParameters() {
        return Stream.of(
                Arguments.of(999L),
                Arguments.of(0L)
        );
    }

    @Test
    public void testGetAllTransactions() {
        TransactionResponse response = transactionService.getAllTransactions(null, 10, 1L);

        List<Transaction> transactions = response.transactions();
        assertEquals(3, transactions.size());
        assertEquals(700, transactions.getFirst().amount());

        Metadata metadata = response.metadata();
        assertEquals(3, metadata.records());
        assertEquals(1L, metadata.lastSeenId());
    }

    @Test
    public void testGetAllTransactions_AccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> transactionService.getAllTransactions(null, 10, 999L));
    }
}