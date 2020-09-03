package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.entity.Account;
import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountService accountService;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    @DisplayName("Given account and total money when do a transfer and account does not exists then get an error")
    void transferMoneyToNonExistingAccount_thenThrowTransactionException() {
        String destinationAccountIban = "ES9833136531281564392210";
        BigDecimal totalToTransfer = BigDecimal.valueOf(10.50);
        when(accountRepository.findOne(any())).thenThrow(TransactionException.class);
        assertThrows(TransactionException.class, () -> accountService.transferMoney(destinationAccountIban, totalToTransfer));
    }

    @Test
    @DisplayName("Given account and total money when do a transfer then set new balance in account")
    void transferMoneyToAnAccount_thenSaveNewBalanceInTheAccount() {
        String destinationAccountIban = "ES9833136531281564392210";
        BigDecimal totalToTransfer = BigDecimal.valueOf(10.50);

        Account destinationAccount = new Account();
        destinationAccount.setBalance(BigDecimal.valueOf(100.00));

        when(accountRepository.findOne(any())).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.saveAndFlush(any())).thenReturn(destinationAccount);

        Account changedAccount = accountService.transferMoney(destinationAccountIban, totalToTransfer);

        assertEquals(BigDecimal.valueOf(110.50), changedAccount.getBalance());

        verify(accountRepository).findOne(any());
        verify(accountRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Given account and total money when do a transfer but final balance of account exceeds the maximum then get an error")
    void transferMoneyToAnAccountAndExceedsMaxBalance_thenThrowTransactionException() {
        String destinationAccountIban = "ES9833136531281564392210";
        BigDecimal totalToTransfer = BigDecimal.valueOf(10.50);

        Account destinationAccount = new Account();
        destinationAccount.setBalance(BigDecimal.valueOf(999_999_990.00));

        when(accountRepository.findOne(any())).thenReturn(Optional.of(destinationAccount));

        assertThrows(TransactionException.class, () -> accountService.transferMoney(destinationAccountIban, totalToTransfer));

        verify(accountRepository).findOne(any());
    }

    @Test
    @DisplayName("Given account and total money when withdraw money from it then set new balance in account")
    void withdrawMoneyFromAccount_thenSaveNewBalanceInTheAccount() {
        Long originAccountId = 1L;
        BigDecimal totalToWithdraw = BigDecimal.valueOf(10.50);

        Account originAccount = new Account();
        originAccount.setBalance(BigDecimal.valueOf(100.00));

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(originAccount));
        when(accountRepository.saveAndFlush(any())).thenReturn(originAccount);

        Account changedAccount = accountService.withdrawMoney(originAccountId, totalToWithdraw);

        assertEquals(BigDecimal.valueOf(89.50), changedAccount.getBalance());

        verify(accountRepository).findById(anyLong());
        verify(accountRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Given insufficient balance account and total money when withdraw money from it then get an error")
    void withdrawMoneyFromAccountButFinalBalanceBelowZero_thenThrowTransactionException() {
        Long originAccountId = 1L;
        BigDecimal totalToWithdraw = BigDecimal.valueOf(200.00);

        Account originAccount = new Account();
        originAccount.setBalance(BigDecimal.valueOf(100.00));

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(originAccount));

        assertThrows(TransactionException.class, () -> accountService.withdrawMoney(originAccountId, totalToWithdraw));

        verify(accountRepository).findById(anyLong());
    }
}