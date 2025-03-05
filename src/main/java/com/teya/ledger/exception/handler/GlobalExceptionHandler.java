package com.teya.ledger.exception.handler;

import com.teya.ledger.exception.AccountNotFoundException;
import com.teya.ledger.exception.InsufficientBalanceException;
import com.teya.ledger.exception.TransactionNotFoundException;
import com.teya.ledger.exception.TransactionProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<Map<String, String>> handleInsufficientBalance(InsufficientBalanceException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TransactionProcessingException.class)
    ResponseEntity<Map<String, String>>  handleTransactionProcessingException(TransactionProcessingException ex){
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<Map<String, String>>  handleAccountNotFoundException(AccountNotFoundException ex){
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    ResponseEntity<Map<String, String>>  handleTransactionNotFoundException(TransactionNotFoundException ex){
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>>  handleException(Exception ex){
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

}
