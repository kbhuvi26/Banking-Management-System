package com.bms.repository;

import com.bms.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setAccountId(rs.getInt("account_id"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setBalanceAfter(rs.getBigDecimal("balance_after"));
        int relatedId = rs.getInt("related_account_id");
        t.setRelatedAccountId(rs.wasNull() ? null : relatedId);
        t.setDescription(rs.getString("description"));
        t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
        return t;
    };

    public Transaction save(Transaction txn) {
        String sql = "INSERT INTO transactions " +
                "(account_id, transaction_type, amount, balance_after, related_account_id, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, txn.getAccountId());
            ps.setString(2, txn.getTransactionType());
            ps.setBigDecimal(3, txn.getAmount());
            ps.setBigDecimal(4, txn.getBalanceAfter());
            if (txn.getRelatedAccountId() != null) {
                ps.setInt(5, txn.getRelatedAccountId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, txn.getDescription());
            return ps;
        }, keyHolder);

        txn.setTransactionId(keyHolder.getKey().intValue());
        return txn;
    }

    public List<Transaction> findByAccountId(Integer accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC, transaction_id DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId);
    }

    public List<Transaction> findRecentByAccountId(Integer accountId, int limit) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? " +
                "ORDER BY transaction_date DESC, transaction_id DESC LIMIT ?";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId, limit);
    }
}
