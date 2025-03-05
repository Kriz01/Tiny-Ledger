package com.teya.ledger.exception;

public class TransactionProcessingException extends RuntimeException {
   public TransactionProcessingException(String message) {
        super(message);
    }
}
