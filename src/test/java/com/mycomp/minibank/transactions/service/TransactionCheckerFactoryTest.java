package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;
import com.mycomp.minibank.transactions.service.checker.AtmTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.ClientTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.InternalTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.TransactionChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

class TransactionCheckerFactoryTest {

    private TransactionCheckerFactory transactionCheckerFactory;

    @BeforeEach
    void setUp() {
        transactionCheckerFactory = new TransactionCheckerFactory();
    }

    @Test
    @DisplayName("Given name of checker when invoked then return its instance o error")
    void getCheckerTests() {
        assertTrue(transactionCheckerFactory.getChecker("CLIENT") instanceof ClientTransactionChecker);
        assertTrue(transactionCheckerFactory.getChecker("ATM") instanceof AtmTransactionChecker);
        assertTrue(transactionCheckerFactory.getChecker("INTERNAL") instanceof InternalTransactionChecker);
        assertThrows(IllegalArgumentException.class, () -> transactionCheckerFactory.getChecker("CHECKER"));
        assertThrows(IllegalArgumentException.class, () -> transactionCheckerFactory.getChecker(null));
    }

    @Test
    @DisplayName("Given a transaction when checked its status then return status response")
    void testCheckers() {
        TransactionChecker clientChecker = transactionCheckerFactory.getChecker("CLIENT");
        TransactionChecker atmChecker = transactionCheckerFactory.getChecker("ATM");
        TransactionChecker internalChecker = transactionCheckerFactory.getChecker("INTERNAL");

        Transaction yesterdayTransaction = createTransaction(now().minusDays(1));
        Transaction todayTransaction = createTransaction(now());
        Transaction tomorrowTransaction = createTransaction(now().plusDays(1));

        assertsTransactionStatus(clientChecker , "SETTLED", yesterdayTransaction);
        assertsTransactionStatus(clientChecker , "PENDING", todayTransaction);
        assertsTransactionStatus(clientChecker , "FUTURE", tomorrowTransaction);

        assertsTransactionStatus(atmChecker , "SETTLED", yesterdayTransaction);
        assertsTransactionStatus(atmChecker , "PENDING", todayTransaction);
        assertsTransactionStatus(atmChecker , "PENDING", tomorrowTransaction);

        assertsTransactionStatus(internalChecker , "SETTLED", yesterdayTransaction);
        assertsTransactionStatus(internalChecker , "PENDING", todayTransaction);
        assertsTransactionStatus(internalChecker , "FUTURE", tomorrowTransaction);

    }

    private void assertsTransactionStatus(TransactionChecker clientChecker,
                                          String status, Transaction transaction) {
        TransactionStatusResponse response = clientChecker.check(transaction);
        assertEquals("A123456789", response.getReference());
        assertEquals(status, response.getStatus());
        assertEquals(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP), response.getAmount());
        if (clientChecker instanceof InternalTransactionChecker) {
            assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), response.getFee());
        }
    }

    private Transaction createTransaction(ZonedDateTime day) {
        Transaction transaction = new Transaction();
        transaction.setReference("A123456789");
        transaction.setTransactionDate(day);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setFee(BigDecimal.ZERO);
        return transaction;
    }
}