package com.mycomp.minibank.transactions.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String field;
    private String reference;
    private String status;
    private String iban;
    private String message;

}
