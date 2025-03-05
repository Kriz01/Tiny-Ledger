package com.teya.ledger.repository;

import java.util.Optional;

public interface AccountRepository {
    Optional<Integer> getBalance(Long accountId);

    void updateBalance(Long accountId, String type, Integer amount);

    boolean accountExists(Long accountId);
}
