package com.mycomp.minibank.transactions.controller;

import com.mycomp.minibank.transactions.exception.TransactionException;
import com.mycomp.minibank.transactions.exception.UnknownParametersException;
import com.mycomp.minibank.transactions.model.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@ControllerAdvice
public class MiniBankControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Object> transactionError(TransactionException txe, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        String field = txe.getField();
        String message = txe.getMessage();
        HttpStatus httpStatus = txe.getHttpStatus();
        if ("reference".equals(field)) {
            errorResponse.setReference(field);
            errorResponse.setStatus(message);
        } else {
            errorResponse.setIban(field);
            errorResponse.setMessage(message);
        }
        return super.handleExceptionInternal(txe, errorResponse, new HttpHeaders(), httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        String message = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        return super.handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        FieldError fieldError = Optional.of(ex).map(MethodArgumentNotValidException::getBindingResult)
                .map(Errors::getFieldError).orElseThrow(UnknownParametersException::new);
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        errorResponse.setField(field);
        errorResponse.setMessage(message);
        return super.handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintValidation(ConstraintViolationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        return super.handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
