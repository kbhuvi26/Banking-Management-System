package com.bms.model;

import java.time.LocalDateTime;

/**
 * Plain Java object (POJO) that represents one row of the "users" table.
 * Because we are using JDBC (not JPA), this class has NO annotations
 * tying it to the database - it is just a container for data. The
 * mapping from a database row to this object happens by hand inside
 * UserRepository (see the RowMapper there).
 */
public class User {

    private Integer userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String password; // stores the SHA-256 hash, never plain text
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Integer userId, String name, String email, String phoneNumber,
                String username, String password, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
