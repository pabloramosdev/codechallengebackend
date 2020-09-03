package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.entity.Account;
import com.mycomp.minibank.transactions.entity.Transaction;
import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.model.TransactionRequest;
import com.mycomp.minibank.transactions.model.TransactionResponse;
import com.mycomp.minibank.transactions.model.TransactionStatusRequest;
import com.mycomp.minibank.transactions.model.TransactionStatusResponse;
import com.mycomp.minibank.transactions.repository.TransactionRepository;
import com.mycomp.minibank.transactions.service.checker.TransactionChecker;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionCheckerFactory transactionCheckerFactory;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService,
                              TransactionCheckerFactory transactionCheckerFactory) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionCheckerFactory = transactionCheckerFactory;
    }

    @Transactional
    public List<TransactionResponse> transactionsFromAccount(String originAccount, String sortType) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), "amount");
        List<Transaction> transactions = transactionRepository.findTransactionByOriginAccountId(originAccount, sort);
        return transactions.stream().map(this::createTransactionResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse createTransaction(Long originAccountId, TransactionRequest transactionRequest) {
        String destinationAccountIban = transactionRequest.getAccountIban();
        BigDecimal amountToTransfer = transactionRequest.getAmount();
        BigDecimal fee = Optional.ofNullable(transactionRequest.getFee()).orElseGet(() -> ZERO);

        BigDecimal totalToTransfer = amountToTransfer.multiply(ONE.subtract(CENT.multiply(fee)));

        Account originAccount = accountService.withdrawMoney(originAccountId, totalToTransfer);
        Account destinationAccount = accountService.transferMoney(destinationAccountIban, totalToTransfer);

        if (destinationAccount.getIban().equals(originAccount.getIban())) {
            throw new TransactionException("account_iban", "Origin account and destination account must be different",
                    HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();

        ZonedDateTime transactionDate = Optional.ofNullable(transaction.getTransactionDate())
                .orElseGet(ZonedDateTime::now);

        transaction.setAmount(amountToTransfer);
        transaction.setFee(fee);
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setTransactionDate(transactionDate);
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        return createTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionStatusResponse checkStatus(TransactionStatusRequest transactionStatusRequest) {
        String transactionReference = transactionStatusRequest.getReference();
        Transaction transaction = transactionRepository.findTransactionByReference(transactionReference)
                .orElseThrow(() -> new TransactionException("reference", "INVALID", HttpStatus.OK));

        String channel = Optional.ofNullable(transactionStatusRequest.getChannel()).orElseGet(() -> "INTERNAL");
        TransactionChecker transactionChecker = transactionCheckerFactory.getChecker(channel);
        return transactionChecker.check(transaction);
    }

    private TransactionResponse createTransactionResponse(Transaction t) {
        return TransactionResponse.builder()
                .reference(t.getReference())
                .destinationAccount(t.getDestinationAccount().getIban())
                .amount(t.getAmount())
                .fee(t.getFee())
                .date(t.getTransactionDate())
                .description(t.getDescription())
                .build();
    }

}
