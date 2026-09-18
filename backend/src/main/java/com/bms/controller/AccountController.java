package com.bms.controller;

import com.bms.dto.Dtos.*;
import com.bms.model.Account;
import com.bms.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST /api/accounts
    @PostMapping
    public ResponseEntity<ApiResponse<Account>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account created = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", created));
    }

    // GET /api/accounts
    @GetMapping
    public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.success("Accounts fetched", accountService.getAllAccounts()));
    }

    // GET /api/accounts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Account>> getAccountById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Account fetched", accountService.getAccountById(id)));
    }

    // GET /api/accounts/number/{accountNumber}
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ApiResponse<Account>> getAccountByNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success("Account fetched", accountService.getAccountByNumber(accountNumber)));
    }

    // GET /api/accounts/user/{userId}  -> all accounts belonging to one user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Account>>> getAccountsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(ApiResponse.success("Accounts fetched", accountService.getAccountsByUserId(userId)));
    }

    // PUT /api/accounts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Account>> updateAccount(@PathVariable Integer id,
                                                                @Valid @RequestBody UpdateAccountRequest request) {
        Account updated = accountService.updateAccount(id, request);
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", updated));
    }

    // DELETE /api/accounts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
