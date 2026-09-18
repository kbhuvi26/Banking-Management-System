package com.bms.controller;

import com.bms.dto.Dtos.*;
import com.bms.model.Transaction;
import com.bms.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // POST /api/transactions/deposit
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Transaction>> deposit(@Valid @RequestBody DepositRequest request) {
        Transaction txn = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", txn));
    }

    // POST /api/transactions/withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Transaction>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        Transaction txn = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", txn));
    }

    // POST /api/transactions/transfer
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction txn = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful", txn));
    }

    // GET /api/transactions/account/{accountId}  -> full history
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getHistory(@PathVariable Integer accountId) {
        return ResponseEntity.ok(ApiResponse.success("Transaction history fetched",
                transactionService.getHistoryForAccount(accountId)));
    }

    // GET /api/transactions/account/{accountId}/recent?limit=5
    @GetMapping("/account/{accountId}/recent")
    public ResponseEntity<ApiResponse<List<Transaction>>> getRecent(
            @PathVariable Integer accountId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Recent transactions fetched",
                transactionService.getRecentForAccount(accountId, limit)));
    }
}
