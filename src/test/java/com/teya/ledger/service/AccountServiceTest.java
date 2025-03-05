package com.teya.ledger.service;

import com.teya.ledger.BaseTest;
import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.repository.AccountRepository;
import com.teya.ledger.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends BaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl balanceService;


    @Test
    void testGetBalanceAccountFound() {

        when(accountRepository.getBalance(TEST_ACCOUNT_ID)).thenReturn(Optional.of(TEST_AMOUNT));
        Integer result = balanceService.getBalance(TEST_ACCOUNT_ID);
        assertEquals(TEST_AMOUNT, result);
        verify(accountRepository).getBalance(TEST_ACCOUNT_ID);
    }

    @Test
    void testGetBalanceAccountNotFound() {
        when(accountRepository.getBalance(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> balanceService.getBalance(TEST_ACCOUNT_ID));

        assertEquals("Account with ID " + TEST_ACCOUNT_ID + " not found", exception.getMessage());
        verify(accountRepository).getBalance(TEST_ACCOUNT_ID);
    }

    @Test
    void testUpdateBalance() {
        doNothing().when(accountRepository).updateBalance(TEST_ACCOUNT_ID, "DEPOSIT", TEST_AMOUNT);
        balanceService.updateBalance(TEST_ACCOUNT_ID, "DEPOSIT", TEST_AMOUNT);
        verify(accountRepository).updateBalance(TEST_ACCOUNT_ID, "DEPOSIT", TEST_AMOUNT);
    }
}
