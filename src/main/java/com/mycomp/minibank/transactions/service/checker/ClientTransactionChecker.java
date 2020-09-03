package com.mycomp.minibank.transactions.service.checker;

import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.CENT;
import static com.mycomp.minibank.transactions.constant.MiniBankConstants.ONE;

public class ClientTransactionChecker implements TransactionChecker {
    @Override
    public TransactionStatusResponse check(Transaction transaction) {
        TransactionStatusResponse response = new TransactionStatusResponse();
        response.setReference(transaction.getReference());
        BigDecimal amount = transaction.getAmount().multiply(ONE.subtract(CENT.multiply(transaction.getFee())));
        response.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
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
