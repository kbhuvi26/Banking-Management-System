package com.bms.repository;

import com.bms.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> new Account(
            rs.getInt("account_id"),
            rs.getString("account_number"),
            rs.getInt("user_id"),
            rs.getString("account_type"),
            rs.getBigDecimal("balance"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public Account save(Account account) {
        String sql = "INSERT INTO accounts (account_number, user_id, account_type, balance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getUserId());
            ps.setString(3, account.getAccountType());
            ps.setBigDecimal(4, account.getBalance());
            return ps;
        }, keyHolder);

        account.setAccountId(keyHolder.getKey().intValue());
        return findById(account.getAccountId()).orElse(account);
    }

    public List<Account> findAll() {
        String sql = "SELECT * FROM accounts ORDER BY account_id";
        return jdbcTemplate.query(sql, accountRowMapper);
    }

    public Optional<Account> findById(Integer id) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper, id).stream().findFirst();
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        return jdbcTemplate.query(sql, accountRowMapper, accountNumber).stream().findFirst();
    }

    public List<Account> findByUserId(Integer userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY account_id";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    public boolean existsByAccountNumber(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, accountNumber);
        return count != null && count > 0;
    }

    public int updateAccountType(Integer accountId, String accountType) {
        String sql = "UPDATE accounts SET account_type = ? WHERE account_id = ?";
        return jdbcTemplate.update(sql, accountType, accountId);
    }

    // Overwrites the balance column - used by TransactionService inside deposit/withdraw/transfer
    public int updateBalance(Integer accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        return jdbcTemplate.update(sql, newBalance, accountId);
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
