# Bankleks 🏦

A fullstack banking application built as a study project using **Lit** on the frontend and **Spring Boot** on the backend. It allows users to manage bank accounts, perform transactions, and administrators to manage users through a control panel.

---

## Tech Stack

### Frontend
- **Lit 3.3** — Native Web Components with TypeScript
- **Vite 8** — Bundler and development server
- **TypeScript 6**

### Backend
- **Spring Boot 4.0.5** — REST API
- **Spring Security** — Authentication and authorization with JWT
- **Hibernate / JPA** — ORM with MySQL
- **Lombok** — Boilerplate reduction
- **JJWT 0.11.5** — JWT generation and validation
- **Java 17**

---

## Architecture

### Frontend — Custom SPA Router

The application is a Single Page Application built without external routing libraries. Navigation is handled via the browser's **History API**, a custom reactive store manages global state, and route guards protect private pages.

```
src/
  app-root.ts              ← root component, manages the router
  middleware.ts            ← authentication and role guards
  router/
    router.ts              ← route definitions with role metadata
  store/
    auth.ts                ← reactive store (observer pattern)
  services/
    auth.service.ts        ← login, register, token refresh
    account.service.ts     ← bank account data
    transaction.service.ts
    admin.service.ts
  components/
    forms/                 ← input-form, select-form, base-form, button-form
    table/                 ← data-table (generic and reusable)
    modal/                 ← edit-transaction-modal
    navbar/                ← nav-bar, nav-link
  pages/
    account-page.ts
    movements-page.ts
    transaction-page.ts
    panel-page.ts          ← admin only
    signin-page.ts
    register-page.ts
```

### Backend — REST API with Spring Boot

```
src/main/java/com/example/back/
  controllers/
    AuthController.java
    TransactionController.java
    AdminController.java
  services/
    AuthService.java
    TransactionService.java
    UserService.java
    AccountService.java
    JwtService.java
    RefreshTokenService.java
  entities/
    user/User.java
    transactions/Account.java
    transactions/Transaction.java
    auth/RefreshToken.java
    auth/Role.java
  dto/
    auth/
    transaction/
    user/
  config/
    SecurityConfig.java
    JwtAuthFilter.java
```

---

## Authentication Flow

The system uses **JWT + Refresh Token with HttpOnly cookie**:

1. User logs in — backend returns JWT in the response body and sets the refresh token as an HttpOnly cookie
2. Frontend stores the JWT **in memory only** (never in localStorage)
3. Every authenticated request sends the JWT via `Authorization: Bearer <token>`
4. Every 14 minutes the frontend automatically refreshes the JWT using the cookie
5. On page reload, `/auth/refresh` is called to restore the session

```
POST /auth/login      → JWT (body) + refreshToken (HttpOnly cookie)
POST /auth/register   → creates user and bank account automatically
POST /auth/refresh    → rotates the JWT using the cookie
POST /auth/logout     → invalidates the refresh token and clears the cookie
```

---

## Roles

| Role | Access |
|------|--------|
| `ROLE_CLIENT` | Account summary, movements, transactions |
| `ROLE_ADMINISTRATOR` | All of the above + admin panel, delete users and transactions |

---

## Features

### Client
- View account balance and bank card details
- Browse paginated movements with perspective (sent/received)
- Perform transactions: deposit, withdrawal and transfer
- Edit the concept of a transaction
- IBAN validation on transfers

### Administrator
- Panel with a full user listing
- Delete users (with cascaded deletion of account and transactions)
- Delete any transaction (with automatic balance reversal)

---

## Data Model

```
User (1) ──── (1) Account
User (1) ──── (N) Transaction
Account (1) ── (N) Transaction [as origin]
Account (1) ── (N) Transaction [as destination]
User (1) ──── (N) RefreshToken
```

A bank account with a unique IBAN is automatically created when a new user registers.

---

## Getting Started

### Requirements
- Node.js 20+
- pnpm
- Java 17+
- MySQL 8+

### Backend

```bash
cd bankleks/back

# Configure your database and JWT secret in src/main/resources/application.properties
# Then run:
./mvnw spring-boot:run
```

### Frontend

```bash
cd bankleks/front

# Install dependencies
pnpm install

# Create .env file
echo "VITE_API_URL=/api" > .env

# Start dev server
pnpm dev
```

Vite's proxy automatically redirects `/api/*` to `http://localhost:8080/*`.

---

## Environment Variables

### Frontend (`.env`)
```env
VITE_API_URL=/api
```

### Backend (`application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bankleks
spring.datasource.username=
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
jwt.secret=
jwt.refresh-expiration-ms=
app.cookie.secure=false
```

> Never commit your `.env` or `application.properties` with real credentials. Both files are gitignored.

---

## Key Technical Decisions

- **No routing library** — the router is built from scratch on top of the History API with chainable guard functions
- **Reactive store without libraries** — a custom observer pattern avoids unnecessary dependencies while enabling cross-component reactivity
- **HttpOnly cookie for refresh token** — JavaScript never accesses the refresh token, eliminating XSS risk
- **JWT in memory** — the access token lives in a module-level variable, never in localStorage or sessionStorage
- **Decoupled components** — forms communicate upward via Custom Events with `bubbles: true` and `composed: true`, following a unidirectional data flow
- **Generic reusable table** — `data-table` accepts columns with custom `render` functions, externally controlled pagination, and edit/delete events
- **Backend-driven authorization** — user identity is always extracted from the JWT on the server side, never trusted from the request body