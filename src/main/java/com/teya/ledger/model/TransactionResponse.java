package com.teya.ledger.model;

import lombok.Builder;

import java.util.List;

public record TransactionResponse(List<Transaction> transactions, Metadata metadata) {
    @Builder
    public TransactionResponse {}
}
