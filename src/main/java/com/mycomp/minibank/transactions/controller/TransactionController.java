package com.mycomp.minibank.transactions.controller;

import com.mycomp.minibank.transactions.model.*;
import com.mycomp.minibank.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.mycomp.minibank.transactions.constant.MiniBankConstants.*;

@Validated
@RestController
@Tag(name = "transactions", description = "Transactions API")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Transaction filter", description = "Filter transactions by iban a order_type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionsFromIban(
            @RequestParam("origin_account") @Pattern(regexp = REGEX_ACCOUNT_IBAN, message = "Invalid IBAN") String originAccount,
            @RequestParam("sort_type") @Pattern(regexp = REGEX_SORT_TYPE, message = "Invalid sort_type") String sortType) {
        return ResponseEntity.ok(transactionService.transactionsFromAccount(originAccount, sortType));
    }

    @Operation(summary = "Checks transaction status", description = "Return status of a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionStatusResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/transactionStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionStatusResponse> getStatus(
            @Valid @RequestBody TransactionStatusRequest transactionStatusRequest) {
        return ResponseEntity.ok(transactionService.checkStatus(transactionStatusRequest));
    }

    @Operation(summary = "Creates a transaction", description = "Creates a transaction between two accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping(value = "/accounts/{accountId}/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> postTransaction(@PathVariable Long accountId,
                                                @Valid @RequestBody TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = transactionService.createTransaction(accountId, transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionResponse);
    }

}
