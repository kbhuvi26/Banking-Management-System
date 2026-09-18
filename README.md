# Banking Management System

A full-stack academic project: **Spring Boot + JDBC** backend, **plain HTML/CSS/JS**
frontend, **MySQL** database. No React, no Thymeleaf, no JPA/Hibernate — every SQL
query is written by hand with `JdbcTemplate` so you can see exactly what's happening.

```
banking-management-system/
├── backend/         Spring Boot project (Maven)
├── database/         schema.sql — run this first
└── frontend/         plain HTML/CSS/JS — open in a browser or serve statically
```

## 1. Architecture at a glance

```
 Browser (HTML/CSS/JS)
        │  fetch() → JSON over REST
        ▼
 Controller   (@RestController) — reads the HTTP request, calls the service, returns JSON
        │
        ▼
 Service      (@Service)        — business rules: validation, hashing, balance math
        │
        ▼
 Repository   (@Repository)     — the ONLY layer that writes SQL, via JdbcTemplate
        │
        ▼
 MySQL
```

Each layer only talks to the one directly below it. A controller never runs SQL, and a
repository never contains business logic — that separation is the whole point of the
Controller → Service → Repository pattern.

**Why JDBC instead of JPA?** With JPA/Hibernate you'd annotate `User` with `@Entity`
and Hibernate would generate SQL for you behind the scenes. Here, `User` is a plain
class with no database annotations, and `UserRepository` writes the `INSERT`/`SELECT`
statements itself using `JdbcTemplate`. You see and control every query.

## 2. Prerequisites

- JDK 17+
- Maven 3.8+ (or use your IDE's built-in Maven)
- MySQL 8+ running locally
- A code editor (IntelliJ IDEA / VS Code recommended)

## 3. Setup — step by step

### Step 1: Create the database
Open MySQL Workbench or the `mysql` CLI and run the whole `database/schema.sql` file:
```bash
mysql -u root -p < database/schema.sql
```
This creates the `banking_management_system` database with the `users`, `accounts`
and `transactions` tables, along with their primary keys, foreign keys and constraints.

### Step 2: Configure the backend
Open `backend/src/main/resources/application.properties` and set your own MySQL
username/password:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### Step 3: Run the backend
```bash
cd backend
mvn spring-boot:run
```
Or open the `backend` folder in IntelliJ/VS Code and run
`BankingManagementSystemApplication.java` directly. The API starts on
**http://localhost:8080**.

### Step 4: Open the frontend
The frontend is plain static files, so you don't need a build step — just open
`frontend/index.html` in a browser, or (recommended, avoids some browser file:// quirks)
serve it with a simple static server, e.g. the VS Code "Live Server" extension, or:
```bash
cd frontend
python3 -m http.server 5500
```
then visit `http://localhost:5500`. `js/api.js` already points at
`http://localhost:8080/api`, and `CorsConfig.java` allows the backend to accept
requests from any origin, so the two can run on different ports without issues.

### Step 5: Try it out
1. Register a new user on `register.html`.
2. Log in on `index.html`.
3. On the **Accounts** page, open a Savings or Current account.
4. On the **Transactions** page, deposit some money, then try a withdrawal or a
   transfer to another account number.
5. Check the **Dashboard** for a live summary.

## 4. Database schema

| Table          | Key columns                                                                 |
|----------------|------------------------------------------------------------------------------|
| `users`        | `user_id` PK · unique `email`, `username` · `password` (SHA-256 hash)        |
| `accounts`     | `account_id` PK · unique `account_number` · `user_id` FK → users · `balance` |
| `transactions` | `transaction_id` PK · `account_id` FK → accounts · `related_account_id` FK (nullable, used for transfers) |

`accounts.user_id` and `transactions.account_id` cascade on delete, so removing a user
removes their accounts, and removing an account removes its transaction log.

## 5. REST API reference

All responses share the shape `{ success, message, data }`. Base URL: `/api`.

| Method | Endpoint                              | Purpose                          |
|--------|----------------------------------------|-----------------------------------|
| POST   | `/users/register`                     | Create a user                     |
| POST   | `/users/login`                        | Validate credentials              |
| GET    | `/users`                              | List all users                    |
| GET    | `/users/{id}`                         | Get one user                      |
| PUT    | `/users/{id}`                         | Update name/email/phone           |
| DELETE | `/users/{id}`                         | Delete a user (cascades accounts) |
| POST   | `/accounts`                           | Open an account                   |
| GET    | `/accounts`                           | List all accounts                 |
| GET    | `/accounts/{id}`                      | Get one account                   |
| GET    | `/accounts/number/{accountNumber}`    | Look up by account number         |
| GET    | `/accounts/user/{userId}`             | All accounts for a user           |
| PUT    | `/accounts/{id}`                      | Update account type               |
| DELETE | `/accounts/{id}`                      | Close an account                  |
| POST   | `/transactions/deposit`               | Deposit money                     |
| POST   | `/transactions/withdraw`              | Withdraw money (checks balance)   |
| POST   | `/transactions/transfer`              | Transfer between two accounts     |
| GET    | `/transactions/account/{accountId}`   | Full transaction history          |
| GET    | `/transactions/account/{accountId}/recent?limit=5` | Recent transactions  |

The `UserController`/`AccountController` expose full CRUD even though the frontend
only wires up self-service screens (view/edit your own profile and accounts) — you can
exercise the rest with Postman/curl, or build an admin page on top of them as an
extension.

## 6. How a deposit actually works (reading the code)

1. `POST /api/transactions/deposit` hits `TransactionController.deposit()`.
2. The controller calls `TransactionService.deposit()`.
3. The service loads the account via `AccountRepository.findById()`, adds the amount
   in Java using `BigDecimal` (never `double`, to avoid rounding errors with money),
   writes the new balance back with `AccountRepository.updateBalance()`, then inserts
   a row into `transactions` via `TransactionRepository.save()`.
4. `@Transactional` on the service method means both the balance update and the
   transaction insert commit together, or neither does, if something fails midway.
5. The controller wraps the result in `ApiResponse.success(...)` and returns HTTP 201.

Transfers work the same way but touch two accounts and write two transaction rows
(`TRANSFER_OUT` on the sender, `TRANSFER_IN` on the receiver) inside one
`@Transactional` method — see `TransactionService.transfer()`.

## 7. Notes on security (read before treating this as production-ready)

This is an academic project, so a few shortcuts were made deliberately, and are worth
knowing about:
- Passwords are hashed with SHA-256 (`PasswordUtil.java`). A real system would use
  BCrypt (via Spring Security) instead, which salts the hash and is deliberately slow.
- There's no session/JWT layer — after login, the frontend simply remembers the user
  object in the browser's `localStorage` (see `js/api.js` → `Session`). Anyone with
  access to the browser's dev tools could edit that data, so don't reuse this pattern
  for anything beyond a classroom project.
- There's no authorization check on the backend — any logged-in-looking client can
  call any user's endpoints. Adding Spring Security would be the natural next step.
