package com.teya.ledger.controller;

import com.teya.ledger.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static com.teya.ledger.constants.Constants.ACCOUNT_ID_PATH;
import static com.teya.ledger.constants.Constants.BALANCE_PATH;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    AccountService accountService;

    private MockMvc mockMvc;

    @InjectMocks
    private AccountController accountController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void getBalanceTest() throws Exception {
        when(accountService.getBalance(1L)).thenReturn(1000);
        mockMvc.perform(get(BALANCE_PATH + ACCOUNT_ID_PATH, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(BigDecimal.valueOf(1000)));
        verify(accountService).getBalance(1L);
    }
}
