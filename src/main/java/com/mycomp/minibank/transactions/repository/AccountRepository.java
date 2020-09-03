package com.mycomp.minibank.transactions.repository;

import com.mycomp.minibank.transactions.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
