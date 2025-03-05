package com.teya.ledger.service;

import com.teya.ledger.BaseTest;
import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.service.impl.TransactionValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.teya.ledger.constants.Constants.ACCOUNT_NOT_FOUND;
import static com.teya.ledger.constants.Constants.INCORRECT_AMOUNT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionValidatorImplTest extends BaseTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionValidatorImpl transactionValidator;

    @ParameterizedTest
    @CsvSource({
            "1, 200",
            "1, 151"
    })
    public void testValidateBalance_ThrowsInsufficientBalanceException(Long accountId, Integer amount) {
        when(accountService.getBalance(TEST_ACCOUNT_ID1)).thenReturn(150); // Balance is 150
        assertThrows(InsufficientBalanceException.class, () -> transactionValidator.validateBalance(accountId, amount), INSUFFICIENT_BALANCE_MSG);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 100",
            "1, 50"
    })
    public void testValidateBalance_DoesNotThrowException(Long accountId, Integer amount) {
        when(accountService.getBalance(TEST_ACCOUNT_ID1)).thenReturn(150); // Balance is 150
        assertDoesNotThrow(() -> transactionValidator.validateBalance(accountId, amount));
    }

    @Test
    public void testValidateAccount_ThrowsAccountNotFoundException() {
        when(accountService.accountExists(TEST_ACCOUNT_ID)).thenReturn(false);
        assertThrows(AccountNotFoundException.class, () -> {
            transactionValidator.validateAccount(TEST_ACCOUNT_ID);
        }, ACCOUNT_NOT_FOUND);
    }

    @Test
    public void testValidateAccount_DoesNotThrowException() {
        when(accountService.accountExists(TEST_ACCOUNT_ID)).thenReturn(true);
        assertDoesNotThrow(() -> transactionValidator.validateAccount(TEST_ACCOUNT_ID));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -100})
    public void testValidateAmount_ThrowsIllegalArgumentException(Integer amount) {
        assertThrows(IllegalArgumentException.class, () -> transactionValidator.validateAmount(amount), INCORRECT_AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000})
    public void testValidateAmount_DoesNotThrowException(Integer amount) {
        assertDoesNotThrow(() -> {
            transactionValidator.validateAmount(amount);
        });
    }


    @ParameterizedTest
    @MethodSource("provideInvalidTransactionParameters")
    public void testValidateTransaction_ThrowsException(Long accountId, Integer amount, TransactionType type, Class<? extends Exception> exceptionClass, String message) {
        if (TransactionType.WITHDRAWAL.equals(type)) {
            when(accountService.getBalance(accountId)).thenReturn(50);
            when(accountService.accountExists(accountId)).thenReturn(true);
        }
        Exception exception = assertThrows(exceptionClass, () -> {
            transactionValidator.validateTransaction(accountId, amount, type);
        });
        assert (exception.getMessage().contains(message));
    }

    @ParameterizedTest
    @MethodSource("provideValidTransactionParameters")
    public void testValidateTransaction_DoesNotThrowException(Long accountId, Integer amount, TransactionType type) {
        when(accountService.accountExists(accountId)).thenReturn(true);
        if (TransactionType.WITHDRAWAL.equals(type)) {
            when(accountService.getBalance(accountId)).thenReturn(250);
        }
        assertDoesNotThrow(() -> {
            transactionValidator.validateTransaction(accountId, amount, type);
        });
    }

    private static Stream<Arguments> provideInvalidTransactionParameters() {
        return Stream.of(
                Arguments.of(1L, 0, TransactionType.DEPOSIT, IllegalArgumentException.class, INCORRECT_AMOUNT),
                Arguments.of(2L, 100, TransactionType.DEPOSIT, AccountNotFoundException.class, ACCOUNT_NOT_FOUND),
                Arguments.of(1L, 200, TransactionType.WITHDRAWAL, InsufficientBalanceException.class, INSUFFICIENT_BALANCE_MSG)
        );
    }

    private static Stream<Arguments> provideValidTransactionParameters() {
        return Stream.of(
                Arguments.of(1L, 100, TransactionType.DEPOSIT),
                Arguments.of(1L, 100, TransactionType.WITHDRAWAL)
        );
    }
}