package com.mycomp.minibank.transactions.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.REGEX_CHANNEL;
import static com.mycomp.minibank.transactions.constant.MiniBankConstants.REGEX_REFERENCE;

@Data
public class TransactionStatusRequest {

    @NotNull
    @Pattern(regexp = REGEX_REFERENCE, message = "Invalid reference format")
    private String reference;
    @Pattern(regexp = REGEX_CHANNEL, message = "channel must be ASC or DESC")
    private String channel;

}
