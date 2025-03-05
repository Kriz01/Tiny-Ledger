package com.teya.ledger.repository.impl;

import com.teya.ledger.model.Status;
import com.teya.ledger.model.Transaction;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.repository.TransactionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.teya.ledger.constants.Constants.*;
import static com.teya.ledger.utils.SqlQueries.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TransactionRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> getAllTransactions(Long lastTransactionId, int limit, Long accountId) {
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue(ACCOUNT_ID, accountId).addValue(LIMIT, limit);

        if (lastTransactionId == null) {
            sql = GET_ALL_TRANSACTIONS_WITHOUT_LAST_ID;
        } else {
            sql = GET_ALL_TRANSACTIONS_WITH_LAST_ID;
            params.addValue(LAST_TRANSACTION_ID, lastTransactionId);
        }

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new Transaction(
                rs.getLong(ID),
                TransactionType.valueOf(rs.getString(TYPE)),
                rs.getInt(AMOUNT),
                rs.getTimestamp(TIMESTAMP).toLocalDateTime(),
                Status.valueOf(rs.getString(STATUS))
        ));
    }

    @Override
    public void insertTransaction(Long accountId, String type, Integer amount, UUID idempotencyKey) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACCOUNT_ID, accountId)
                .addValue(TYPE, type)
                .addValue(AMOUNT, amount)
                .addValue(IDEMPOTENCY_KEY, idempotencyKey);

        jdbcTemplate.update(INSERT_TRANSACTION, params);
    }

    @Override
    public void updateStatus(UUID idempotencyKey, String status) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(STATUS, status)
                .addValue(IDEMPOTENCY_KEY, idempotencyKey);

        jdbcTemplate.update(UPDATE_TRANSACTION_STATUS, params);
    }

    @Override
    public Optional<String> getStatusByIdempotencyKey(UUID idempotencyKey) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(IDEMPOTENCY_KEY, idempotencyKey);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY, params, String.class));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
