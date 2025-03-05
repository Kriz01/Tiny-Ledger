package com.teya.ledger.service;

import com.teya.ledger.model.TransactionMessageResponse;
import com.teya.ledger.model.TransactionResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse getAllTransactions(Long lastTransactionId, int limit, Long accountId);

    ResponseEntity<TransactionMessageResponse> depositAmount(Long accountId, Integer amount, UUID idempotencyKey);

    ResponseEntity<TransactionMessageResponse> withdrawAmount(Long accountId, Integer amount, UUID idempotencyKey);

}
