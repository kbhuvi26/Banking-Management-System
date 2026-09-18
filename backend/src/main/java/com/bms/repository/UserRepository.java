package com.bms.repository;

import com.bms.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * This is the ONLY class in the whole project that talks SQL directly.
 * It uses Spring's JdbcTemplate, which is a thin wrapper around plain
 * java.sql.Connection/PreparedStatement/ResultSet that removes the
 * boilerplate (opening/closing connections, catching SQLException, etc.)
 * while still letting you write the SQL yourself - this is the key
 * difference from JPA/Hibernate, where the SQL is generated for you.
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // Spring automatically injects the JdbcTemplate bean it configured
    // from the spring.datasource.* properties in application.properties.
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Converts one row of a ResultSet into a User object.
    // This is the "manual mapping" step that JPA would normally do for you.
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone_number"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public User save(User user) {
        String sql = "INSERT INTO users (name, email, phone_number, username, password) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPassword());
            return ps;
        }, keyHolder);

        user.setUserId(keyHolder.getKey().intValue());
        return findById(user.getUserId()).orElse(user);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, username);
        return results.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
        return results.stream().findFirst();
    }

    // Used at login: accepts either the username OR the email
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, usernameOrEmail, usernameOrEmail);
        return results.stream().findFirst();
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public int update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone_number = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, user.getName(), user.getEmail(),
                user.getPhoneNumber(), user.getUserId());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
