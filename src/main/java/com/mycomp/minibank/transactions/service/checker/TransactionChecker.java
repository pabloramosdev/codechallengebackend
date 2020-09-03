package com.mycomp.minibank.transactions.service.checker;

import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;

public interface TransactionChecker {

    TransactionStatusResponse check(Transaction transaction);

}
