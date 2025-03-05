package com.teya.ledger.controller;

import com.teya.ledger.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import static com.teya.ledger.constants.Constants.ACCOUNT_ID_PATH;
import static com.teya.ledger.constants.Constants.BALANCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"/db/test-schema.sql", "/db/test-data.sql"})
public class AccountControllerIntegrationIT extends BaseTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String url;


    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + port + "/api/teya" + BALANCE_PATH + ACCOUNT_ID_PATH;
    }

    @Test
    void getBalanceIntegrationTest() {
        Integer balance = 800;
        ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class, TEST_ACCOUNT_ID1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
    }
}
