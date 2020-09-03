package com.mycomp.minibank.transactions.repository;

import com.mycomp.minibank.transactions.entity.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    @Query("select t from Transaction t join fetch t.originAccount account where account.iban = :iban")
    List<Transaction> findTransactionByOriginAccountId(@Param("iban") String iban, Sort sort);

    @Query("select t from Transaction t where t.reference = :reference")
    Optional<Transaction> findTransactionByReference(@Param("reference") String transactionReference);
}
