package com.mycomp.minibank.transactions.entity;

import lombok.extern.slf4j.Slf4j;
import javax.persistence.PrePersist;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.TRANSACTION_CODE;

@Slf4j
public class TransactionListener {

    @PrePersist
    public void setReferenceAfterPersist(Transaction transaction) {
        if (transaction.getReference() == null) {
            int nanoSeconds = transaction.getTransactionDate().getNano();
            transaction.setReference(generateReferenceValue(nanoSeconds));
        }
    }

    private String generateReferenceValue(int nanoSeconds) {
        String counter = String.valueOf(nanoSeconds);
        String paddedCounter = String.format("%9s", counter).replace(" ", "0");
        return TRANSACTION_CODE + paddedCounter;
    }

}
