package com.teya.ledger.util;

import com.teya.ledger.model.TransactionMessageResponse;
import com.teya.ledger.utils.TransactionUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "COMPLETED, OK, 'Transaction already completed.'",
            "PENDING, CONFLICT, 'Transaction is still being processed.'",
            "FAILED, INTERNAL_SERVER_ERROR, 'Transaction failed previously. Please try again.'",
            "INVALID_STATUS, BAD_REQUEST, 'Invalid transaction status.'"
    })
    void testHandleTransactionStatus(String status, HttpStatus expectedStatus, String expectedMessage) {
        ResponseEntity<TransactionMessageResponse> response = TransactionUtils.handleTransactionStatus(status);
        assertEquals(expectedStatus, response.getStatusCode());
        assertEquals(expectedMessage, Objects.requireNonNull(response.getBody()).message());
    }
}
