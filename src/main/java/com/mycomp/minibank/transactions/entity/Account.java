package com.mycomp.minibank.transactions.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iban")
    private String iban;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "cancellation_date")
    private ZonedDateTime cancellationDate;

    @OneToMany(mappedBy = "originAccount", fetch = FetchType.LAZY)
    private List<Transaction> transactionsEmitted;

    @OneToMany(mappedBy = "destinationAccount", fetch = FetchType.LAZY)
    private List<Transaction> transactionsReceived;

}
