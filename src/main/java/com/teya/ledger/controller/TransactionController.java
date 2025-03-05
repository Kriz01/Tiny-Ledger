package com.teya.ledger.controller;

import com.teya.ledger.model.AmountRequest;
import com.teya.ledger.model.TransactionMessageResponse;
import com.teya.ledger.model.TransactionResponse;
import com.teya.ledger.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.teya.ledger.constants.Constants.*;

@RestController
@RequestMapping(TRANSACTION_PATH)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(HISTORY_PATH + ACCOUNT_ID_PATH)
    TransactionResponse getTransactionHistory(@RequestParam(required = false) Long lastTransactionId, @RequestParam int limit, @PathVariable(ACCOUNT_ID) Long accountId) {
        return transactionService.getAllTransactions(lastTransactionId, limit, accountId);
    }

    @PostMapping(DEPOSIT_PATH)
    public ResponseEntity<TransactionMessageResponse> depositAmount(@PathVariable(ACCOUNT_ID) Long accountId, @RequestBody AmountRequest amountRequest, @RequestHeader(IDEMPOTENCY_KEY_HEADER) UUID idempotencyKey) {
        return transactionService.depositAmount(accountId, amountRequest.amount(), idempotencyKey);
    }

    @PostMapping(WITHDRAW_PATH)
    public ResponseEntity<TransactionMessageResponse> withdrawAmount(@PathVariable(ACCOUNT_ID) Long accountId, @RequestBody AmountRequest amountRequest,
                                                                     @RequestHeader(IDEMPOTENCY_KEY_HEADER) UUID idempotencyKey) {
        return transactionService.withdrawAmount(accountId, amountRequest.amount(), idempotencyKey);
    }
}
