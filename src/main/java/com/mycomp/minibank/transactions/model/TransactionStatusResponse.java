package com.mycomp.minibank.transactions.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionStatusResponse {

    private String reference;
    private String status;
    private BigDecimal amount;
    private BigDecimal fee;

}
