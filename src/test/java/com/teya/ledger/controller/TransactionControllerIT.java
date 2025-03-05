package com.teya.ledger.controller;

import com.teya.ledger.BaseTest;
import com.teya.ledger.model.AmountRequest;
import com.teya.ledger.model.TransactionMessageResponse;
import com.teya.ledger.model.TransactionResponse;
import com.teya.ledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static com.teya.ledger.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"/db/test-schema.sql", "/db/test-data.sql"})
public class TransactionControllerIT extends BaseTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/api/teya" + TRANSACTION_PATH;
    }

    @ParameterizedTest
    @CsvSource({
            "5, ",
            "10, 3",
            "20, 2",
            "2, ",
            "2, 3"
    })
    void getTransactionHistoryTest(int limit, Long lastTransactionId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + HISTORY_PATH + "/account/" + TEST_ACCOUNT_ID1)
                .queryParam(LIMIT, limit);

        if (lastTransactionId != null) {
            builder.queryParam(LAST_TRANSACTION_ID, lastTransactionId);
        }

        String url = builder.encode().toUriString();

        ResponseEntity<TransactionResponse> response = restTemplate.getForEntity(url, TransactionResponse.class);

        assertEquals(200, response.getStatusCode().value());

        assertEquals(getExpectedTransactions(lastTransactionId, limit), Objects.requireNonNull(response.getBody()).transactions());
    }

    @ParameterizedTest
    @CsvSource({
            "DEPOSIT, 200, 201",
            "WITHDRAW, 100, 201"
    })
    void transactionTestForDepositAndWithdrawal(String transactionType, int amount, int expectedStatus) {
        AmountRequest amountRequest = new AmountRequest(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.set(IDEMPOTENCY_KEY_HEADER, TEST_IDEMPOTENCY_KEY.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AmountRequest> requestEntity = new HttpEntity<>(amountRequest, headers);

        String url;
        if (TransactionType.DEPOSIT.name().equals(transactionType)) {
            url = baseUrl + DEPOSIT_PATH.replace("{accountId}", TEST_ACCOUNT_ID1.toString());
        } else {
            url = baseUrl + WITHDRAW_PATH.replace("{accountId}", TEST_ACCOUNT_ID1.toString());
        }

        ResponseEntity<TransactionMessageResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, TransactionMessageResponse.class
        );

        assertEquals(expectedStatus, response.getStatusCode().value());

        assertEquals(TRANSACTION_COMPLETED_MSG, Objects.requireNonNull(response.getBody()).message());
    }

}
