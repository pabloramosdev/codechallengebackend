package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.entity.Account;
import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.model.TransactionRequest;
import com.mycomp.minibank.transactions.model.TransactionResponse;
import com.mycomp.minibank.transactions.model.TransactionStatusRequest;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;
import com.mycomp.minibank.transactions.repository.TransactionRepository;
import com.mycomp.minibank.transactions.service.checker.InternalTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.TransactionChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionService transactionService;

    private AccountService accountService;

    private TransactionRepository transactionRepository;

    private TransactionCheckerFactory transactionCheckerFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        accountService = mock(AccountService.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionCheckerFactory = mock(TransactionCheckerFactory.class);
        transactionService = new TransactionService(transactionRepository, accountService,transactionCheckerFactory);
    }

    @Test
    @DisplayName("Given account and sorting type when search transactions then return ordered transactions")
    void transactionsFromAccountShouldReturnTransactionsOrderedByAmount() {
        String originAccount = "ES9833136531281564392210";
        String sortType = "ASC";

        Transaction orderedTransaction = new Transaction();
        Account destinationAccount = new Account();
        destinationAccount.setIban("ES9888616014223812241566");
        orderedTransaction.setDestinationAccount(destinationAccount);
        when(transactionRepository.findTransactionByOriginAccountId(anyString(), any()))
                .thenReturn(Collections.singletonList(orderedTransaction));

        List<TransactionResponse> actualOrderedTransactions =
                transactionService.transactionsFromAccount(originAccount, sortType);

        assertEquals(1, actualOrderedTransactions.size());

        verify(transactionRepository).findTransactionByOriginAccountId(anyString(), any());
    }

    @Test
    @DisplayName("Given account id and transaction request when create transaction then return transaction response")
    void createTransactionShouldReturnTransactionResponse() {
        Long originAccountId = 1L;
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountIban("ES9888616014223812241566");
        transactionRequest.setAmount(BigDecimal.TEN);

        Account originAccount = new Account();
        Account destinationAccount = new Account();
        originAccount.setIban("ES9833136531281564392210");
        destinationAccount.setIban("ES9888616014223812241566");

        Transaction actualTransaction = new Transaction();
        actualTransaction.setDestinationAccount(destinationAccount);

        when(accountService.withdrawMoney(anyLong(), any(BigDecimal.class))).thenReturn(originAccount);
        when(accountService.transferMoney(anyString(), any(BigDecimal.class))).thenReturn(destinationAccount);
        when(transactionRepository.save(any())).thenReturn(actualTransaction);

        transactionService.createTransaction(originAccountId, transactionRequest);

        verify(accountService).withdrawMoney(anyLong(), any(BigDecimal.class));
        verify(accountService).transferMoney(anyString(), any(BigDecimal.class));
        verify(transactionRepository).save(any());
    }

    @Test
    @DisplayName("Given account id and transaction request to original account when create transaction then return error")
    void createTransactionToSameAccountShouldReturnTransactionException() {
        Long originAccountId = 1L;
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountIban("ES9833136531281564392210");
        transactionRequest.setAmount(BigDecimal.TEN);

        Account originAccount = new Account();
        Account destinationAccount = new Account();
        originAccount.setIban("ES9833136531281564392210");
        destinationAccount.setIban("ES9833136531281564392210");

        when(accountService.withdrawMoney(anyLong(), any(BigDecimal.class))).thenReturn(originAccount);
        when(accountService.transferMoney(anyString(), any(BigDecimal.class))).thenReturn(destinationAccount);

        assertThrows(TransactionException.class, () ->
                transactionService.createTransaction(originAccountId, transactionRequest));

        verify(accountService).withdrawMoney(anyLong(), any(BigDecimal.class));
        verify(accountService).transferMoney(anyString(), any(BigDecimal.class));
    }


    @Test
    @DisplayName("Given status request when check status then return status response")
    void checkStatusShouldReturnStatusOfTransaction() {
        TransactionStatusRequest transactionRequest = new TransactionStatusRequest();
        transactionRequest.setReference("INTERNAL");

        TransactionChecker internalCheckerMock = mock(InternalTransactionChecker.class);
        when(transactionRepository.findTransactionByReference(anyString())).thenReturn(Optional.of(new Transaction()));
        when(transactionCheckerFactory.getChecker(anyString())).thenReturn(internalCheckerMock);
        when(internalCheckerMock.check(any(Transaction.class))).thenReturn(new TransactionStatusResponse());

        transactionService.checkStatus(transactionRequest);

        verify(transactionRepository).findTransactionByReference(anyString());
        verify(transactionCheckerFactory).getChecker(anyString());
        verify(internalCheckerMock).check(any(Transaction.class));
    }

    @Test
    @DisplayName("Given unknown reference when check status then return invalid result")
    void checkStatusOfUnknownReferenceShouldReturnTransactionException() {
        TransactionStatusRequest transactionRequest = new TransactionStatusRequest();
        transactionRequest.setReference("INTERNAL");

        TransactionChecker internalCheckerMock = mock(InternalTransactionChecker.class);
        when(transactionRepository.findTransactionByReference(anyString())).thenThrow(TransactionException.class);

        assertThrows(TransactionException.class, () -> transactionService.checkStatus(transactionRequest));

        verify(transactionRepository).findTransactionByReference(anyString());
    }

}