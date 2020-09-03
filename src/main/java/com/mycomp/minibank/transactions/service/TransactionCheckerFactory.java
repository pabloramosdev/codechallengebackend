package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.service.checker.AtmTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.ClientTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.InternalTransactionChecker;
import com.mycomp.minibank.transactions.service.checker.TransactionChecker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TransactionCheckerFactory {

    private final Map<String, TransactionChecker> statusCheckerMap = new HashMap<>();

    public TransactionCheckerFactory() {
        statusCheckerMap.put("CLIENT", new ClientTransactionChecker());
        statusCheckerMap.put("ATM", new AtmTransactionChecker());
        statusCheckerMap.put("INTERNAL", new InternalTransactionChecker());
    }

    public TransactionChecker getChecker(String channel) {
        if (channel == null || !statusCheckerMap.containsKey(channel)) {
            throw new IllegalArgumentException("Invalid " + channel);
        }
        return statusCheckerMap.get(channel);
    }

}
