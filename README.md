# FinFlow – Loan Application & Approval Management System

A learning-oriented Java Spring Boot backend project demonstrating **OOP**, **SOLID principles**, **Gang of Four design patterns**, **REST APIs**, **database integration**, and **unit testing** — with a minimal React frontend.

> ⚠️ **Educational Project**: All eligibility calculations are simulated for learning purposes. They are not real financial credit decisions.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture & Package Structure](#architecture--package-structure)
- [Design Patterns](#design-patterns)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start (Office Laptop Setup)](#quick-start-office-laptop-setup)
- [Database Setup](#database-setup)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

FinFlow is a Loan Application and Approval Management System that supports:

**Customer Features:**
- View available loan products (Personal, Home, Education)
- Submit loan applications with income and tenure details
- Check eligibility via simulated evaluation rules
- Track application status and history

**Admin Features:**
- View all loan applications
- Approve or reject applications
- View activity audit trail and notification logs

---

## Architecture & Package Structure

```
backend/src/main/java/com/finflow/
├── controller/          # REST endpoints (LoanController, AdminController)
├── service/             # Business logic (LoanService)
├── repository/          # Spring Data JPA repositories
├── model/               # JPA entities (Customer, LoanProduct, LoanApplication)
├── dto/                 # Request/Response DTOs
├── factory/             # Factory Pattern – loan product creation
├── strategy/            # Strategy Pattern – eligibility evaluation
├── observer/            # Observer Pattern – status change notifications
├── decorator/           # Decorator Pattern – logging evaluator wrapper
├── exception/           # Global exception handling (@RestControllerAdvice)
└── FinFlowApplication.java
```

```
frontend/src/
├── api/                 # Axios API client (loanApi.js)
├── components/          # React components
│   ├── ProductCatalog.jsx
│   ├── ApplicationForm.jsx
│   ├── ApplicationTracker.jsx
│   └── AdminDashboard.jsx
├── App.jsx              # Main app with tab navigation
├── main.jsx             # ReactDOM entry point
└── index.css            # Styles
```

---

## Design Patterns

### 1. Factory Pattern – Loan Product Creation

**Problem:** Creating different loan product types (Personal, Home, Education) without scattering `new` calls and type-specific logic across the service layer.

**Implementation:**
- `LoanProductFactory` – common interface with `createProduct()` and `getLoanType()`
- `PersonalLoanFactory`, `HomeLoanFactory`, `EducationLoanFactory` – concrete factories
- `LoanProductFactoryRegistry` – Spring-managed registry that auto-discovers all factory beans

**Key classes:** `com.finflow.factory.*`

### 2. Strategy Pattern – Eligibility Evaluation

**Problem:** Each loan type has different eligibility rules (income thresholds, credit score requirements, debt ratios). Using if-else blocks makes the code rigid and hard to extend.

**Implementation:**
- `EligibilityStrategy` – common interface with `evaluate(application)` and `getLoanType()`
- `PersonalLoanEligibilityStrategy` – checks income ≥ ₹25,000, credit score ≥ 650
- `HomeLoanEligibilityStrategy` – checks income ≥ ₹50,000, credit score ≥ 700
- `EducationLoanEligibilityStrategy` – checks income ≥ ₹15,000, credit score ≥ 600
- `EligibilityStrategyRegistry` – dynamically selects the correct strategy based on loan type

**Key classes:** `com.finflow.strategy.*`

### 3. Observer Pattern – Status Change Notifications

**Problem:** When an application status changes, multiple subsystems (audit log, notifications) need to react without coupling them to the service.

**Implementation:**
- `LoanObserver` – observer interface with `onStatusChange()`
- `LoanActivityObserver` – records timestamped activity log entries
- `LoanNotificationObserver` – generates customer notification messages
- `LoanApplicationSubject` – subject that notifies all registered observers

**Key classes:** `com.finflow.observer.*`

### 4. Decorator Pattern – Logging Evaluator

**Problem:** Adding cross-cutting concerns (logging, timing) to the eligibility evaluator without modifying its core logic.

**Implementation:**
- `LoanEvaluator` – interface with `evaluate(application)`
- `BasicLoanEvaluator` – core evaluation logic
- `LoggingLoanEvaluator` – decorator that wraps BasicLoanEvaluator, adding execution-time logging

**Key classes:** `com.finflow.decorator.*`

### 5. Singleton Pattern – Spring Bean Scope

**Demonstration:** Spring's default singleton scope ensures only one `LoanService` instance exists. The `/api/loans/diagnostic/singleton` endpoint returns the `identityHashCode` of the bean — every request returns the same value, proving shared instance reuse.

**Key endpoint:** `GET /api/loans/diagnostic/singleton`

---

## Technology Stack

| Layer      | Technology                                           |
|------------|------------------------------------------------------|
| Backend    | Java 21, Spring Boot 3.4.3, Spring Web, Spring Data JPA, Hibernate |
| Database   | MySQL 8, H2 (test)                                   |
| Frontend   | React 18, Vite 6, Axios, JavaScript                  |
| Testing    | JUnit 5, Mockito, Spring Boot Test, MockMvc           |
| Build      | Maven 3.9.6 (via Maven Wrapper)                      |

---

## Prerequisites

Before running this project, ensure you have:

| Tool       | Version  | Check Command       |
|------------|----------|---------------------|
| Git        | 2.x+     | `git --version`     |
| Java JDK   | 21+      | `java -version`     |
| MySQL      | 8.x      | `mysql --version`   |
| Node.js    | 18+      | `node -v`           |
| npm        | 9+       | `npm -v`            |

> **Note:** Maven is NOT required globally. The project includes a Maven Wrapper (`mvnw.cmd` / `mvnw`) that automatically downloads Maven 3.9.6.

---

## Quick Start (Office Laptop Setup)

```powershell
# 1. Clone the repository
git clone https://github.com/ja616/moneyview.git
cd moneyview

# 2. Verify tools
java -version       # Must be 21+
node -v             # Must be 18+
npm -v

# 3. Set up MySQL (see Database Setup section below)

# 4. Configure environment
copy .env.example .env
# Edit .env with your MySQL password

# 5. Build & run backend (Terminal 1)
cd backend
.\mvnw.cmd clean verify          # Compile + run all tests
.\mvnw.cmd spring-boot:run       # Start the server on port 8080

# 6. Install & run frontend (Terminal 2)
cd frontend
npm ci                            # Install dependencies from lockfile
npm run dev                       # Start dev server on port 5173
```

**URLs:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api/loans/products

---

## Database Setup

### 1. Install MySQL 8

Download from https://dev.mysql.com/downloads/installer/ and install.

### 2. Create the database

```sql
mysql -u root -p

CREATE DATABASE finflow_db;
SHOW DATABASES;    -- Verify finflow_db appears
EXIT;
```

### 3. Configure credentials

Copy the environment template and fill in your MySQL password:

```powershell
copy .env.example .env
```

Edit `.env`:
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/finflow_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_actual_password
```

Alternatively, set them as system environment variables or pass them as JVM args:
```powershell
$env:SPRING_DATASOURCE_PASSWORD = "your_password"
.\mvnw.cmd spring-boot:run
```

> **Note:** The application uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created automatically on first run. No manual schema creation is needed.

---

## Backend Setup

### Maven Wrapper

The project includes the official Maven Wrapper. No global Maven installation is needed.

```powershell
cd backend

# Check Maven version (downloads Maven on first run)
.\mvnw.cmd -v

# Compile
.\mvnw.cmd clean compile

# Run all tests
.\mvnw.cmd test

# Full build (compile + test + package)
.\mvnw.cmd clean verify

# Start the application
.\mvnw.cmd spring-boot:run
```

On Unix/macOS:
```bash
chmod +x mvnw
./mvnw clean verify
./mvnw spring-boot:run
```

---

## Frontend Setup

```powershell
cd frontend

# Install dependencies (uses package-lock.json for reproducible builds)
npm ci

# Start development server
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

The Vite dev server proxies `/api` requests to `http://localhost:8080`, so the backend must be running first.

---

## Running the Application

### Start both services

**Terminal 1 – Backend:**
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
Wait for: `Started FinFlowApplication in X.XX seconds`

**Terminal 2 – Frontend:**
```powershell
cd frontend
npm run dev
```
Open http://localhost:5173 in your browser.

### UI Tabs
- **Products** – Browse loan products (Factory Pattern in action)
- **Apply** – Submit a loan application
- **Track** – Search by ID, run eligibility evaluation, view observer logs
- **Admin** – List all applications, approve/reject, view Singleton diagnostic

---

## API Endpoints

### Customer Endpoints (`/api/loans`)

| Method | Endpoint                              | Description                     |
|--------|---------------------------------------|---------------------------------|
| GET    | `/api/loans/products`                 | List all loan products          |
| POST   | `/api/loans/applications`             | Submit a new application        |
| GET    | `/api/loans/applications`             | List all applications           |
| GET    | `/api/loans/applications/{id}`        | Get application by ID           |
| POST   | `/api/loans/applications/{id}/evaluate` | Run eligibility evaluation    |
| GET    | `/api/loans/applications/{id}/activities` | Get activity log (Observer) |
| GET    | `/api/loans/applications/{id}/notifications` | Get notifications (Observer) |
| GET    | `/api/loans/diagnostic/singleton`     | Singleton bean diagnostic       |

### Admin Endpoints (`/api/admin`)

| Method | Endpoint                              | Description                     |
|--------|---------------------------------------|---------------------------------|
| PATCH  | `/api/admin/applications/{id}/approve`| Approve an application          |
| PATCH  | `/api/admin/applications/{id}/reject` | Reject an application           |

### Example: Submit a Loan Application

```bash
curl -X POST http://localhost:8080/api/loans/applications \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Walker",
    "customerEmail": "alice@example.com",
    "monthlyIncome": 75000,
    "loanType": "PERSONAL",
    "requestedAmount": 200000,
    "tenureMonths": 24
  }'
```

### Example: Evaluate Eligibility

```bash
curl -X POST http://localhost:8080/api/loans/applications/1/evaluate
```

### Example: Approve Application

```bash
curl -X PATCH http://localhost:8080/api/admin/applications/1/approve
```

---

## Testing

### Run all backend tests

```powershell
cd backend
.\mvnw.cmd test
```

### Test suite (29 tests across 7 test classes)

| Test Class                      | Tests | Covers                                |
|---------------------------------|-------|---------------------------------------|
| `LoanFactoryTest`              | 3     | Factory pattern – product creation     |
| `EligibilityStrategyTest`      | 5     | Strategy pattern – evaluation rules    |
| `LoanObserverTest`             | 3     | Observer pattern – notifications       |
| `LoggingLoanEvaluatorTest`     | 2     | Decorator pattern – logging wrapper    |
| `LoanServiceTest`              | 8     | Service layer business logic           |
| `LoanControllerTest`           | 5     | REST controller (MockMvc)              |
| `LoanRepositoryTest`           | 3     | JPA repository integration (H2)       |

Tests use an in-memory H2 database (configured in `src/test/resources/application-test.properties`), so MySQL is not required for testing.

---

## Troubleshooting

### "Network Error" in the frontend
The Spring Boot backend is not running. Start it first:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### MySQL connection refused
1. Ensure MySQL is running: `mysql -u root -p`
2. Check your `.env` file has the correct password
3. Verify the database exists: `SHOW DATABASES;`

### Maven Wrapper SSL error
If the wrapper cannot download Maven due to SSL/certificate issues on a corporate network:
```powershell
# Option 1: Download Maven manually and add to PATH
# Option 2: Use the JVM trust store flag
$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT"
.\mvnw.cmd -v
```

### npm install SSL error (`UNABLE_TO_VERIFY_LEAF_SIGNATURE`)
This happens on corporate networks with TLS inspection:
```powershell
npm config set strict-ssl false
npm install
# Re-enable after install:
npm config delete strict-ssl
```

### Java version mismatch
The project requires Java 21+. Check with `java -version`. Download from https://jdk.java.net/21/ or https://www.oracle.com/java/technologies/downloads/.

### Port already in use
If port 8080 is busy, change it in `backend/src/main/resources/application.properties`:
```properties
server.port=8081
```

---

## Project Structure

```
moneyview/
├── backend/                      # Spring Boot application
│   ├── src/main/java/com/finflow/  # Source code
│   ├── src/main/resources/         # application.properties
│   ├── src/test/                   # Unit & integration tests
│   ├── .mvn/wrapper/               # Maven Wrapper config
│   ├── mvnw.cmd                    # Maven Wrapper (Windows)
│   ├── mvnw                        # Maven Wrapper (Unix)
│   └── pom.xml                     # Maven build config
├── frontend/                     # React + Vite app
│   ├── src/                        # React components & API client
│   ├── package.json                # npm dependencies
│   ├── package-lock.json           # Lockfile for reproducible builds
│   ├── vite.config.js              # Vite config with API proxy
│   └── index.html                  # HTML entry point
├── .env.example                  # Environment config template
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

---

## License

This is an educational project. No license restrictions.
