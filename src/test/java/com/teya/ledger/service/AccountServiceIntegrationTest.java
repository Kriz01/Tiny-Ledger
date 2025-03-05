package com.teya.ledger.service;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.repository.AccountRepository;
import com.teya.ledger.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    public void setup() {
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    public void testGetBalance() {
        Integer balance = accountService.getBalance(1L);
        assertEquals(800, balance);
    }

    @Test
    public void testGetBalance_AccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> accountService.getBalance(999L));
    }

    @Test
    public void testUpdateBalance_Deposit() {
        accountService.updateBalance(1L, TransactionType.DEPOSIT.name(), 100);
        Integer balance = accountService.getBalance(1L);
        assertEquals(900, balance);
    }

    @Test
    public void testUpdateBalance_Withdraw() {
        accountService.updateBalance(1L, TransactionType.WITHDRAWAL.name(), 500);
        Integer balance = accountService.getBalance(1L);
        assertEquals(300, balance);
    }
}