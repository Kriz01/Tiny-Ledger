package com.teya.ledger.repository.impl;

import com.teya.ledger.model.TransactionType;
import com.teya.ledger.repository.AccountRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.teya.ledger.constants.Constants.ACCOUNT_ID;
import static com.teya.ledger.constants.Constants.AMOUNT;
import static com.teya.ledger.utils.SqlQueries.*;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AccountRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Integer> getBalance(Long accountId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue(ACCOUNT_ID, accountId);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(GET_BALANCE, params, Integer.class));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void updateBalance(Long accountId, String type, Integer amount) {
        String updateBalanceSql;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACCOUNT_ID, accountId)
                .addValue(AMOUNT, amount);

        updateBalanceSql = switch (TransactionType.valueOf(type)) {
            case DEPOSIT -> UPDATE_BALANCE_DEPOSIT;
            case WITHDRAWAL -> UPDATE_BALANCE_WITHDRAWAL;
        };

        jdbcTemplate.update(updateBalanceSql, params);
    }

    @Override
    public boolean accountExists(Long accountId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue(ACCOUNT_ID, accountId);
        Boolean exists = jdbcTemplate.queryForObject(ACCOUNT_EXISTS, params, Boolean.class);
        return exists != null && exists;
    }
}
