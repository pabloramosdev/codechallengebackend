package com.mycomp.minibank.transactions.controller;

import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.model.TransactionRequest;
import com.mycomp.minibank.transactions.model.TransactionResponse;
import com.mycomp.minibank.transactions.model.TransactionStatusRequest;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;
import com.mycomp.minibank.transactions.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {

    private static final String GET_TRANSACTIONS_BY_FILTER = "/transactions";

    private static final String POST_TRANSACTION_STATUS = "/transactionStatus";

    private static final String POST_TRANSACTION_CREATION = "/accounts/{accountId}/transactions";

    private static final ZonedDateTime date =
            ZonedDateTime.parse("2018-09-16T08:50:32+02:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    private MockMvc mockMvc;

    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        transactionService = Mockito.mock(TransactionService.class);
        TransactionController transactionController = new TransactionController(transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new MiniBankControllerAdvice()).build();
    }

    @Test
    @DisplayName("Given iban and sort type when request for transactions then obtain ok")
    public void whenGetTransactionsSorted_thenObtainListOfTransactions() throws Exception {
        when(transactionService.transactionsFromAccount(anyString(), anyString()))
                .thenReturn(Collections.singletonList(createTransactionResponse()));
        mockMvc.perform(get(GET_TRANSACTIONS_BY_FILTER)
                .param("origin_account","ES9833136531281564392210")
                .param("sort_type","ASC"))
                .andExpect(status().isOk());
        verify(transactionService).transactionsFromAccount(anyString(), anyString());
    }

    @Test
    @DisplayName("Given reference and channel when check transaction status then obtain ok")
    public void whenPostTransactionsStatus_thenObtainStatus() throws Exception {
        when(transactionService.checkStatus(any(TransactionStatusRequest.class)))
                .thenReturn(new TransactionStatusResponse());

        mockMvc.perform(post(POST_TRANSACTION_STATUS)
                .content("{" +
                        "\"reference\": \"A977000000\"," +
                        "\"channel\": \"CLIENT\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(transactionService).checkStatus(any(TransactionStatusRequest.class));
    }

    @Test
    @DisplayName("Given transaction data when save transaction then obtain ok")
    public void whenPostTransactions_thenObtainTransactionSaved() throws Exception {
        when(transactionService.createTransaction(anyLong(), any(TransactionRequest.class)))
                .thenReturn(createTransactionResponse());

        mockMvc.perform(post(POST_TRANSACTION_CREATION, 1L)
                .content("{   \n" +
                        "    \"account_iban\": \"ES9888616014223812241566\",\n" +
                        "    \"amount\": \"450.32\",\n" +
                        "    \"fee\": \"5.00\",\n" +
                        "    \"description\": \"Alquiler Piso\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(transactionService).createTransaction(anyLong(), any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Given reference and bad channel when check transaction status then obtain bad request")
    public void whenPostTransactionsStatusNonExistingChannel_thenBadRequest() throws Exception {
        when(transactionService.checkStatus(any(TransactionStatusRequest.class)))
                .thenThrow(new TransactionException("reference", "INVALID", HttpStatus.OK));

        mockMvc.perform(post(POST_TRANSACTION_STATUS)
                .content("{" +
                        "\"reference\": \"A977000000\"," +
                        "\"channel\": \"BAD_CHANNEL\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given account iban without sort param when check transaction status then obtain bad request")
    public void whenGetTransactionsParameterMissing_thenBadRequest() throws Exception {
        mockMvc.perform(get(GET_TRANSACTIONS_BY_FILTER)
                .param("origin_account","ES9833136531281564392210"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given transaction to same account when create transaction then obtain bad request")
    public void whenPostTransactionsToSameAccount_thenObtainBadRequest() throws Exception {
        when(transactionService.createTransaction(anyLong(), any(TransactionRequest.class)))
                .thenThrow(new TransactionException("account_iban", "Origin account and destination account must be different",
                        HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(POST_TRANSACTION_CREATION, 1L)
                .content("{   \n" +
                        "    \"account_iban\": \"ES9833136531281564392210\",\n" +
                        "    \"amount\": \"450.32\",\n" +
                        "    \"fee\": \"5.00\",\n" +
                        "    \"description\": \"Alquiler Piso\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionService).createTransaction(anyLong(), any(TransactionRequest.class));
    }

    private TransactionResponse createTransactionResponse() {
        return TransactionResponse.builder()
                .reference("A123456789")
                .description("Transaction")
                .amount(BigDecimal.ZERO)
                .fee(BigDecimal.ZERO)
                .destinationAccount("ES9833136531281564392210")
                .date(date)
                .build();
    }

}
