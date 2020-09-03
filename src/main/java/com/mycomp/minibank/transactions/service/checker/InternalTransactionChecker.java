package com.mycomp.minibank.transactions.service.checker;

import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InternalTransactionChecker implements TransactionChecker {
    @Override
    public TransactionStatusResponse check(Transaction transaction) {
        TransactionStatusResponse response = new TransactionStatusResponse();
        response.setReference(transaction.getReference());
        response.setAmount(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        response.setFee(transaction.getFee().setScale(2, BigDecimal.ROUND_HALF_UP));
        LocalDate date = transaction.getTransactionDate().toLocalDate();
        if (date.isBefore(LocalDate.now())) {
            response.setStatus("SETTLED");
        } else if (date.isEqual(LocalDate.now())) {
            response.setStatus("PENDING");
        } else {
            response.setStatus("FUTURE");
        }
        return response;
    }
}
