package com.teya.ledger.utils;

import com.teya.ledger.model.Status;
import com.teya.ledger.model.TransactionMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.teya.ledger.constants.Constants.*;

public class TransactionUtils {

    private TransactionUtils() {
    }

    /**
     * Handles the transaction status and returns an appropriate HTTP response.
     * <p>
     * This method converts the provided status string into an enumerated {@link Status} value
     * and returns a corresponding HTTP response with a relevant message.
     * </p>
     * <ul>
     *     <li>{@code COMPLETED} → Returns HTTP 200 (OK) with a message indicating the transaction is already completed.</li>
     *     <li>{@code PENDING} → Returns HTTP 409 (Conflict) with a message indicating the transaction is still pending.</li>
     *     <li>{@code FAILED} → Returns HTTP 500 (Internal Server Error) with a message indicating the transaction failed.</li>
     *     <li>Invalid status → Returns HTTP 400 (Bad Request) if the provided status does not match any valid enum value.</li>
     * </ul>
     *
     * @param status The transaction status as a string.
     * @return {@link ResponseEntity} containing a {@link TransactionMessageResponse} with an appropriate status message.
     */
    public static ResponseEntity<TransactionMessageResponse> handleTransactionStatus(String status) {
        try {
            Status transactionStatus = Status.valueOf(status);
            return switch (transactionStatus) {
                case COMPLETED -> ResponseEntity.status(HttpStatus.OK)
                        .body(new TransactionMessageResponse(TRANSACTION_ALREADY_COMPLETED_MSG));
                case PENDING -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new TransactionMessageResponse(TRANSACTION_PENDING_MSG));
                case FAILED -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new TransactionMessageResponse(TRANSACTION_FAILED_MSG));
            };
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TransactionMessageResponse(INVALID_TRANSACTION_STATUS_MSG));
        }
    }
}
