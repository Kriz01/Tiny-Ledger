package com.teya.ledger.repository;

import com.teya.ledger.BaseTest;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.repository.impl.AccountRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Optional;
import java.util.stream.Stream;

import static com.teya.ledger.utils.SqlQueries.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest extends BaseTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private AccountRepositoryImpl accountRepository;

    @Test
    void getBalance_shouldReturnBalance_whenAccountExists() {
        when(jdbcTemplate.queryForObject(eq(GET_BALANCE), any(MapSqlParameterSource.class), eq(Integer.class)))
                .thenReturn(TEST_AMOUNT);

        Optional<Integer> balance = accountRepository.getBalance(TEST_ACCOUNT_ID);

        assertTrue(balance.isPresent());
        assertEquals(TEST_AMOUNT, balance.get());
        verify(jdbcTemplate).queryForObject(eq(GET_BALANCE), any(MapSqlParameterSource.class), eq(Integer.class));
    }

    @Test
    void getBalance_shouldReturnEmpty_whenAccountDoesNotExist() {
        when(jdbcTemplate.queryForObject(eq(GET_BALANCE), any(MapSqlParameterSource.class), eq(Integer.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Integer> balance = accountRepository.getBalance(TEST_ACCOUNT_ID);

        assertFalse(balance.isPresent());
        verify(jdbcTemplate).queryForObject(eq(GET_BALANCE), any(MapSqlParameterSource.class), eq(Integer.class));
    }

    @ParameterizedTest
    @CsvSource({"DEPOSIT", "WITHDRAWAL"})
    void updateBalance_shouldUpdateBalance(String type) {
        String expectedQuery;
        if (type.equals(TransactionType.DEPOSIT.name())) {
            expectedQuery = UPDATE_BALANCE_DEPOSIT;
        } else {
            expectedQuery = UPDATE_BALANCE_WITHDRAWAL;
        }
        when(jdbcTemplate.update(eq(expectedQuery), any(MapSqlParameterSource.class))).thenReturn(1);
        accountRepository.updateBalance(TEST_ACCOUNT_ID, type, TEST_AMOUNT);
        verify(jdbcTemplate).update(eq(expectedQuery), any(MapSqlParameterSource.class));
    }


    @ParameterizedTest
    @MethodSource("provideAccountExistsParameters")
    void accountExists_shouldReturnExpectedResult(Boolean queryResult, boolean expectedResult) {
        when(jdbcTemplate.queryForObject(eq(ACCOUNT_EXISTS), any(MapSqlParameterSource.class), eq(Boolean.class)))
                .thenReturn(queryResult);
        boolean exists = accountRepository.accountExists(TEST_ACCOUNT_ID);
        assertEquals(expectedResult, exists);
        verify(jdbcTemplate).queryForObject(eq(ACCOUNT_EXISTS), any(MapSqlParameterSource.class), eq(Boolean.class));
    }

    private static Stream<Arguments> provideAccountExistsParameters() {
        return Stream.of(
                Arguments.of(true, true),
                Arguments.of(false, false),
                Arguments.of(null, false)
        );
    }
}