package com.mycomp.minibank.transactions.model;

import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.*;

@Data
public class TransactionRequest {

    @Pattern(regexp = REGEX_REFERENCE, message = "Invalid reference format")
    private String reference;

    @NotNull
    @Digits(integer = 5, fraction = 2, message = "amount must be a value from 0.00 to 99999.99")
    private BigDecimal amount;

    @NotNull
    @Pattern(regexp = REGEX_ACCOUNT_IBAN, message = "Invalid IBAN format")
    private String accountIban;

    @DecimalMin(value = "-100.00", message = "minimum value for fee is -100.00")
    @DecimalMax(value = "100.00", message = "maximum value for fee is 100.00")
    @Digits(integer = 3, fraction = 2, message = "fee must have format (-)###.##")
    private BigDecimal fee;

    @Size(max = 50, message = "description has a max length of 50 characters")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "transaction_date must be a present value or future value")
    private ZonedDateTime transactionDate;

}
