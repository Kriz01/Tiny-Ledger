package com.teya.ledger.model;

import java.time.LocalDateTime;

public record Transaction(Long id, TransactionType type, Integer amount, LocalDateTime timestamp,Status status) {
}
