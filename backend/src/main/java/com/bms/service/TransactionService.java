package com.bms.service;

import com.bms.dto.Dtos.DepositRequest;
import com.bms.dto.Dtos.TransferRequest;
import com.bms.dto.Dtos.WithdrawRequest;
import com.bms.exception.InsufficientBalanceException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.model.Account;
import com.bms.model.Transaction;
import com.bms.repository.AccountRepository;
import com.bms.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * @Transactional makes the balance update and the transaction-log insert
     * happen as a single all-or-nothing database transaction: if anything
     * fails halfway through, Spring rolls back every change made in this
     * method so the account balance and the transaction log never get out
     * of sync.
     */
    @Transactional
    public Transaction deposit(DepositRequest request) {
        Account account = getAccountOrThrow(request.getAccountId());

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        accountRepository.updateBalance(account.getAccountId(), newBalance);

        Transaction txn = new Transaction();
        txn.setAccountId(account.getAccountId());
        txn.setTransactionType("DEPOSIT");
        txn.setAmount(request.getAmount());
        txn.setBalanceAfter(newBalance);
        txn.setDescription(request.getDescription() != null ? request.getDescription() : "Cash deposit");

        return transactionRepository.save(txn);
    }

    @Transactional
    public Transaction withdraw(WithdrawRequest request) {
        Account account = getAccountOrThrow(request.getAccountId());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + account.getBalance() + ", requested: " + request.getAmount());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        accountRepository.updateBalance(account.getAccountId(), newBalance);

        Transaction txn = new Transaction();
        txn.setAccountId(account.getAccountId());
        txn.setTransactionType("WITHDRAW");
        txn.setAmount(request.getAmount());
        txn.setBalanceAfter(newBalance);
        txn.setDescription(request.getDescription() != null ? request.getDescription() : "Cash withdrawal");

        return transactionRepository.save(txn);
    }

    /**
     * A transfer touches two accounts, so it creates two transaction rows:
     * a TRANSFER_OUT on the sender and a TRANSFER_IN on the receiver. Both
     * balance updates and both inserts happen inside one @Transactional
     * method for the same all-or-nothing reason described above.
     */
    @Transactional
    public Transaction transfer(TransferRequest request) {
        Account fromAccount = getAccountOrThrow(request.getFromAccountId());
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destination account not found: " + request.getToAccountNumber()));

        if (fromAccount.getAccountId().equals(toAccount.getAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + fromAccount.getBalance() + ", requested: " + request.getAmount());
        }

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());

        accountRepository.updateBalance(fromAccount.getAccountId(), fromNewBalance);
        accountRepository.updateBalance(toAccount.getAccountId(), toNewBalance);

        String description = request.getDescription() != null
                ? request.getDescription()
                : "Transfer to " + toAccount.getAccountNumber();

        Transaction outTxn = new Transaction();
        outTxn.setAccountId(fromAccount.getAccountId());
        outTxn.setTransactionType("TRANSFER_OUT");
        outTxn.setAmount(request.getAmount());
        outTxn.setBalanceAfter(fromNewBalance);
        outTxn.setRelatedAccountId(toAccount.getAccountId());
        outTxn.setDescription(description);
        Transaction savedOutTxn = transactionRepository.save(outTxn);

        Transaction inTxn = new Transaction();
        inTxn.setAccountId(toAccount.getAccountId());
        inTxn.setTransactionType("TRANSFER_IN");
        inTxn.setAmount(request.getAmount());
        inTxn.setBalanceAfter(toNewBalance);
        inTxn.setRelatedAccountId(fromAccount.getAccountId());
        inTxn.setDescription("Transfer from " + fromAccount.getAccountNumber());
        transactionRepository.save(inTxn);

        // Return the sender-side transaction record to the caller
        return savedOutTxn;
    }

    public List<Transaction> getHistoryForAccount(Integer accountId) {
        getAccountOrThrow(accountId);
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> getRecentForAccount(Integer accountId, int limit) {
        getAccountOrThrow(accountId);
        return transactionRepository.findRecentByAccountId(accountId, limit);
    }

    private Account getAccountOrThrow(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }
}
