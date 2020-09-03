package com.mycomp.minibank.transactions.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class TransactionResponse {

    private String reference;
    private String destinationAccount;
    private ZonedDateTime date;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;

}
