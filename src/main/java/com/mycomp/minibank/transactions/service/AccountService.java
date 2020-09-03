package com.mycomp.minibank.transactions.service;

import com.mycomp.minibank.transactions.entity.Account;
import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.repository.AccountRepository;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.math.BigDecimal;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.MAX_BALANCE_PERMITTED;
import static com.mycomp.minibank.transactions.constant.MiniBankConstants.ZERO;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findAccountByIban(String iban) {
        Account account = new Account();
        account.setIban(iban);
        return accountRepository.findOne(Example.of(account))
                .orElseThrow(() -> new TransactionException(iban, "Account not found", HttpStatus.OK));
    }

    public Account transferMoney(String destinationAccountIban, BigDecimal totalToTransfer) {
        Account destinationAccount = findAccountByIban(destinationAccountIban);
        BigDecimal balanceBeforeTransfer = destinationAccount.getBalance();
        BigDecimal balanceAfterTransfer = balanceBeforeTransfer.add(totalToTransfer);
        if (balanceAfterTransfer.compareTo(MAX_BALANCE_PERMITTED) > 0) {
            throw new TransactionException(destinationAccountIban, "Maximum balance reached", HttpStatus.OK);
        }
        destinationAccount.setBalance(balanceAfterTransfer);
        return accountRepository.saveAndFlush(destinationAccount);
    }

    public Account withdrawMoney(Long originAccountId, BigDecimal totalToWithdraw) {
        Account originAccount = accountRepository.findById(originAccountId)
                .orElseThrow(() -> new TransactionException("origin_account", "origin account not found", HttpStatus.OK));
        BigDecimal balanceBeforeWithdraw = originAccount.getBalance();
        BigDecimal balanceAfterWithdraw = balanceBeforeWithdraw.subtract(totalToWithdraw);
        if (balanceAfterWithdraw.compareTo(ZERO) < 0) {
            throw new TransactionException(originAccount.getIban(), "Insufficient balance", HttpStatus.OK);
        }
        originAccount.setBalance(balanceAfterWithdraw);
        return accountRepository.saveAndFlush(originAccount);
    }
}
