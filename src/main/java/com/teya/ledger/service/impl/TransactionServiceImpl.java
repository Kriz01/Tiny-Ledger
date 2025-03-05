package com.teya.ledger.service.impl;

import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.exception.TransactionNotFoundException;
import com.teya.ledger.exception.TransactionProcessingException;
import com.teya.ledger.model.*;
import com.teya.ledger.repository.TransactionRepository;
import com.teya.ledger.service.AccountService;
import com.teya.ledger.service.TransactionService;
import com.teya.ledger.service.TransactionValidator;
import com.teya.ledger.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.teya.ledger.constants.Constants.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionValidator transactionValidator;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService, TransactionValidator transactionValidator) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionValidator = transactionValidator;
    }

    /**
     * Processes a deposit transaction for the specified account.
     * <p>
     * The <b>amount</b> is specified in <b>minor units</b> (e.g., cents for USD) to avoid floating-point precision issues
     * that can arise when dealing with monetary values. Using integers ensures accurate calculations and avoids
     * rounding errors that are common with floating-point arithmetic.
     * </p>
     *
     * @param accountId      The ID of the account to deposit into.
     * @param amount         The amount to deposit, specified in minor units (e.g., cents for USD).
     * @param idempotencyKey A unique key to ensure idempotency of the request.
     * @return A ResponseEntity containing a success or error message.
     */
    @Transactional
    @Override
    public ResponseEntity<TransactionMessageResponse> depositAmount(Long accountId, Integer amount, UUID idempotencyKey) {
        return processTransaction(accountId, amount, idempotencyKey, TransactionType.DEPOSIT);
    }

    /**
     * Processes a withdrawal transaction for the specified account.
     *
     * @param accountId      The ID of the account to withdraw from.
     * @param amount         The amount to withdraw.
     * @param idempotencyKey A unique key to ensure idempotency of the request.
     * @return A ResponseEntity containing a success or error message.
     * @throws InsufficientBalanceException If the account has insufficient balance for the withdrawal.
     */
    @Transactional
    @Override
    public ResponseEntity<TransactionMessageResponse> withdrawAmount(Long accountId, Integer amount, UUID idempotencyKey) {
        return processTransaction(accountId, amount, idempotencyKey, TransactionType.WITHDRAWAL);
    }

    /**
     * Retrieves a paginated list of transactions for the specified account,
     * starting from the last seen transaction ID.
     * <p>
     * This method implements pagination to efficiently fetch a subset of transactions
     * rather than retrieving the entire history at once. It returns transactions in batches,
     * helping optimize performance for large datasets.
     * </p>
     *
     * @param lastTransactionId The ID of the last transaction seen (used for pagination, optional).
     * @param limit             The maximum number of transactions to return per page.
     * @param accountId         The ID of the account to retrieve transactions for.
     * @return A {@link TransactionResponse} containing the paginated list of transactions and metadata.
     * @throws TransactionNotFoundException If no transactions are found for the account.
     */
    @Override
    public TransactionResponse getAllTransactions(Long lastTransactionId, int limit, Long accountId) {
        transactionValidator.validateAccount(accountId);
        List<Transaction> transactions = transactionRepository.getAllTransactions(lastTransactionId, limit, accountId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(TRANSACTION_NOT_FOUND + accountId);
        }
        int records = transactions.size();
        return TransactionResponse.builder()
                .transactions(transactions)
                .metadata(new Metadata(records, transactions.getLast().id()))
                .build();

    }

    /**
     * Processes a transaction (deposit or withdrawal) for the specified account.
     *
     * @param accountId      The ID of the account to process the transaction for.
     * @param amount         The amount to process.
     * @param idempotencyKey A unique key to ensure idempotency of the request.
     * @param type           The type of transaction (DEPOSIT or WITHDRAWAL).
     * @return A ResponseEntity containing a success or error message.
     * @throws TransactionProcessingException If the transaction processing fails.
     */
    private ResponseEntity<TransactionMessageResponse> processTransaction(Long accountId, Integer amount, UUID idempotencyKey, TransactionType type) {
        Optional<ResponseEntity<TransactionMessageResponse>> statusResponse = getTransactionStatusByIdempotencyKey(idempotencyKey);
        if (statusResponse.isPresent()) {
            return statusResponse.get();
        }
        transactionValidator.validateTransaction(accountId, amount, type);
        try {
            executeTransaction(accountId, type.name(), amount, idempotencyKey);
            return createSuccessResponse();
        } catch (Exception ex) {
            throw new TransactionProcessingException(TRANSACTION_PROCESSING_FAILED + ex.getMessage());
        }
    }

    /**
     * Executes a transaction by inserting it into the database and updating the account balance.
     *
     * @param accountId      The ID of the account to process the transaction for.
     * @param type           The type of transaction (DEPOSIT or WITHDRAWAL).
     * @param amount         The amount to process.
     * @param idempotencyKey A unique key to ensure idempotency of the request.
     * @throws InsufficientBalanceException If the account has insufficient balance for a withdrawal.
     */
    private synchronized void executeTransaction(Long accountId, String type, Integer amount, UUID idempotencyKey) throws InsufficientBalanceException {
        transactionRepository.insertTransaction(accountId, type, amount, idempotencyKey);
        accountService.updateBalance(accountId, type, amount);
        transactionRepository.updateStatus(idempotencyKey, Status.COMPLETED.name());
    }

    /**
     * Retrieves the status of a transaction by its idempotency key.
     *
     * @param idempotencyKey The unique key associated with the transaction.
     * @return An Optional containing the ResponseEntity with the transaction status, if found.
     */
    public Optional<ResponseEntity<TransactionMessageResponse>> getTransactionStatusByIdempotencyKey(UUID idempotencyKey) {
        Optional<String> status = transactionRepository.getStatusByIdempotencyKey(idempotencyKey);
        return status.map(TransactionUtils::handleTransactionStatus);
    }

    /**
     * Creates a success response for a completed transaction.
     *
     * @return A ResponseEntity with a success message and HTTP status 201 (CREATED).
     */
    private ResponseEntity<TransactionMessageResponse> createSuccessResponse() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TransactionMessageResponse(TRANSACTION_COMPLETED_MSG));
    }

}
