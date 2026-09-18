package com.bms.service;

import com.bms.dto.Dtos.CreateAccountRequest;
import com.bms.dto.Dtos.UpdateAccountRequest;
import com.bms.exception.ResourceNotFoundException;
import com.bms.model.Account;
import com.bms.repository.AccountRepository;
import com.bms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(CreateAccountRequest request) {
        // Make sure the user actually exists before creating an account for them
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setUserId(request.getUserId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());

        return accountRepository.save(account);
    }

    // Generates a random 12-digit account number and retries if it happens to collide
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            StringBuilder sb = new StringBuilder("AC");
            for (int i = 0; i < 10; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            accountNumber = sb.toString();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
    }

    public List<Account> getAccountsByUserId(Integer userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account updateAccount(Integer id, UpdateAccountRequest request) {
        getAccountById(id); // throws if not found
        accountRepository.updateAccountType(id, request.getAccountType());
        return getAccountById(id);
    }

    public void deleteAccount(Integer id) {
        getAccountById(id);
        accountRepository.deleteById(id);
    }
}
