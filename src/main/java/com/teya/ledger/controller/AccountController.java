package com.teya.ledger.controller;

import com.teya.ledger.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.teya.ledger.constants.Constants.*;

@RestController
@RequestMapping(BALANCE_PATH)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(ACCOUNT_ID_PATH)
    Integer getBalance(@PathVariable(ACCOUNT_ID) Long accountId) {
        return accountService.getBalance(accountId);
    }
}
