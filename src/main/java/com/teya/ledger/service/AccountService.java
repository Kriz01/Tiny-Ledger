package com.teya.ledger.service;

public interface AccountService {

    Integer getBalance(Long accountId);

    void updateBalance(Long accountId, String type, Integer amount);

    boolean accountExists(Long accountId);
}
