package com.bms.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * All request/response DTOs (Data Transfer Objects) are kept in this one
 * file to keep the project easy to navigate for a beginner. A DTO is just
 * a plain class shaped like the JSON the frontend sends or expects back -
 * it is separate from the "model" classes, which are shaped like the
 * database tables.
 */
public class Dtos {

    // ---------- Auth ----------

    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 50, message = "Username must be at least 4 characters")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        private String usernameOrEmail;

        @NotBlank(message = "Password is required")
        private String password;

        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateUserRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    // ---------- Accounts ----------

    public static class CreateAccountRequest {
        @NotNull(message = "userId is required")
        private Integer userId;

        @NotBlank(message = "accountType is required")
        @Pattern(regexp = "SAVINGS|CURRENT", message = "accountType must be SAVINGS or CURRENT")
        private String accountType;

        @DecimalMin(value = "0.0", inclusive = true, message = "Initial deposit cannot be negative")
        private BigDecimal initialDeposit = BigDecimal.ZERO;

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
        public BigDecimal getInitialDeposit() { return initialDeposit; }
        public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
    }

    public static class UpdateAccountRequest {
        @NotBlank(message = "accountType is required")
        @Pattern(regexp = "SAVINGS|CURRENT", message = "accountType must be SAVINGS or CURRENT")
        private String accountType;

        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
    }

    // ---------- Transactions ----------

    public static class DepositRequest {
        @NotNull(message = "accountId is required")
        private Integer accountId;

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        private BigDecimal amount;

        private String description;

        public Integer getAccountId() { return accountId; }
        public void setAccountId(Integer accountId) { this.accountId = accountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class WithdrawRequest {
        @NotNull(message = "accountId is required")
        private Integer accountId;

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        private BigDecimal amount;

        private String description;

        public Integer getAccountId() { return accountId; }
        public void setAccountId(Integer accountId) { this.accountId = accountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TransferRequest {
        @NotNull(message = "fromAccountId is required")
        private Integer fromAccountId;

        @NotNull(message = "toAccountNumber is required")
        private String toAccountNumber;

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        private BigDecimal amount;

        private String description;

        public Integer getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Integer fromAccountId) { this.fromAccountId = fromAccountId; }
        public String getToAccountNumber() { return toAccountNumber; }
        public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // ---------- Generic API response wrapper ----------

    /**
     * Every endpoint returns this same shape so the frontend can always
     * check `success` and read either `data` or `message` consistently.
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse() {
        }

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}
