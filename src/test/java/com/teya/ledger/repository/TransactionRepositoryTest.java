package com.teya.ledger.repository;

import com.teya.ledger.BaseTest;
import com.teya.ledger.model.Status;
import com.teya.ledger.model.Transaction;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.repository.impl.TransactionRepositoryImpl;
import com.teya.ledger.utils.SqlQueries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.teya.ledger.utils.SqlQueries.GET_ALL_TRANSACTIONS_WITHOUT_LAST_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryTest extends BaseTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;
    @InjectMocks
    private TransactionRepositoryImpl transactionRepository;

    @Test
    void getAllTransactions_shouldReturnTransactions_whenValidParams() {
        int limit = 10;

        List<Transaction> expectedTransactions = List.of(transaction);

        when(jdbcTemplate.query(
                eq(GET_ALL_TRANSACTIONS_WITHOUT_LAST_ID),
                any(MapSqlParameterSource.class),
                ArgumentMatchers.<RowMapper<Transaction>>any()
        )).thenReturn(expectedTransactions);

        List<Transaction> transactions = transactionRepository.getAllTransactions(null, limit, TEST_ACCOUNT_ID1);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.getFirst());
    }

    @Test
    void insertTransaction_shouldInsertTransaction() {
        transactionRepository.insertTransaction(TEST_ACCOUNT_ID1, TransactionType.WITHDRAWAL.name(), TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);

        verify(jdbcTemplate).update(eq(SqlQueries.INSERT_TRANSACTION), any(MapSqlParameterSource.class));
    }

    @Test
    void updateStatus_shouldUpdateStatus() {
        transactionRepository.updateStatus(TEST_IDEMPOTENCY_KEY, Status.COMPLETED.name());
        verify(jdbcTemplate).update(eq(SqlQueries.UPDATE_TRANSACTION_STATUS), any(MapSqlParameterSource.class));
    }

    @Test
    void getStatusByIdempotencyKey_shouldReturnStatus_whenRecordExists() {

        when(jdbcTemplate.queryForObject(eq(SqlQueries.GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY), any(MapSqlParameterSource.class), eq(String.class)))
                .thenReturn(Status.COMPLETED.name());
        Optional<String> status = transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY);
        assertTrue(status.isPresent());
        assertEquals(Status.COMPLETED.name(), status.get());

        verify(jdbcTemplate).queryForObject(eq(SqlQueries.GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY), any(MapSqlParameterSource.class), eq(String.class));
    }

    @Test
    void getStatusByIdempotencyKey_shouldReturnEmpty_whenRecordDoesNotExist() {
        when(jdbcTemplate.queryForObject(eq(SqlQueries.GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY), any(MapSqlParameterSource.class), eq(String.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        Optional<String> status = transactionRepository.getStatusByIdempotencyKey(TEST_IDEMPOTENCY_KEY);

        assertFalse(status.isPresent());

        verify(jdbcTemplate).queryForObject(eq(SqlQueries.GET_TRANSACTION_STATUS_BY_IDEMPOTENCY_KEY), any(MapSqlParameterSource.class), eq(String.class));
    }
}
