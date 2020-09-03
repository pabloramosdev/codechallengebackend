package com.mycomp.minibank.transactions.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TransactionException extends RuntimeException {

    private final String field;
    private final String message;
    private final HttpStatus httpStatus;

    public TransactionException(String field, String message, HttpStatus httpStatus) {
        this.field = field;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
