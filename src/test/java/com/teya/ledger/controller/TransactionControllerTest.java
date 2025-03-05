package com.teya.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teya.ledger.BaseTest;
import com.teya.ledger.model.TransactionMessageResponse;
import com.teya.ledger.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static com.teya.ledger.constants.Constants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class TransactionControllerTest extends BaseTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;


    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDepositAmountSuccess() throws Exception {
        Long accountId = 123L;
        Integer amount = 1000;
        UUID idempotencyKey = UUID.randomUUID();


        when(transactionService.depositAmount(accountId, amount, idempotencyKey)).thenReturn(
                ResponseEntity.status(HttpStatus.CREATED).body(new TransactionMessageResponse(TRANSACTION_COMPLETED_MSG)));

        mockMvc.perform(post(TRANSACTION_PATH + DEPOSIT_PATH, accountId)
                        .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(TRANSACTION_COMPLETED_MSG));

        verify(transactionService, times(1)).depositAmount(accountId, amount, idempotencyKey);
    }

    @Test
    void testWithdrawAmountSuccess() throws Exception {

        when(transactionService.withdrawAmount(TEST_ACCOUNT_ID1, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY)).thenReturn(
                ResponseEntity.status(HttpStatus.CREATED).body(new TransactionMessageResponse(WITHDRAWAL_SUCCESS_MSG))
        );

        mockMvc.perform(post(TRANSACTION_PATH + WITHDRAW_PATH, TEST_ACCOUNT_ID1)
                        .header(IDEMPOTENCY_KEY_HEADER, TEST_IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(WITHDRAWAL_SUCCESS_MSG));

        verify(transactionService, times(1)).withdrawAmount(TEST_ACCOUNT_ID1, TEST_AMOUNT, TEST_IDEMPOTENCY_KEY);
    }


    @Test
    void testGetTransactionHistorySuccess() throws Exception {
        int limit = 10;
        Long lastTransactionId = 3L;
        var expectedResponse = createTransactionResponse(lastTransactionId, limit);
        when(transactionService.getAllTransactions(lastTransactionId, limit, TEST_ACCOUNT_ID1)).thenReturn(expectedResponse);

        mockMvc.perform(get(TRANSACTION_PATH + HISTORY_PATH + ACCOUNT_ID_PATH, 1L)
                        .param(LIMIT, String.valueOf(limit))
                        .param(LAST_TRANSACTION_ID, String.valueOf(lastTransactionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].id").value(2L))
                .andExpect(jsonPath("$.transactions[0].amount").value(700))
                .andExpect(jsonPath("$.metadata.records").value(2));

        verify(transactionService, times(1)).getAllTransactions(lastTransactionId, limit, TEST_ACCOUNT_ID1);
    }


    @Test
    void testWithdrawAmountMissingIdempotencyKey() throws Exception {
        mockMvc.perform(post(TRANSACTION_PATH + WITHDRAW_PATH, TEST_ACCOUNT_ID1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountRequest)))
                .andExpect(status().isBadRequest());
        verify(transactionService, times(0)).withdrawAmount(any(), any(), any());
    }
}
