# Mini Payroll & Attendance System

A full-stack MVP to manage employees, attendance, leave and payroll.
Built with Java (Spring Boot), Angular and MySQL.

## Live Demo

- Frontend: [https://payroll-ui-sigma.vercel.app]
- Backend API: [https://payroll-backend-production-aec37446.up.railway.app]
- Health check: [ https://payroll-backend-production-aec37446.up.railway.app]/actuator/health
- Frontend repo: [https://github.com/MohamedShajith1262/payroll-ui]
- Admin login page: [https://payroll-ui-sigma.vercel.app]/admin-login
- Admin credentials: shared separately in the submission email

> Note: free hosting tiers can sleep when idle, so the first request may take a few seconds.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Data JPA, Spring Security, Bean Validation |
| Auth | JWT (jjwt), BCrypt password hashing |
| Database | MySQL, Flyway migrations |
| Frontend | Angular (standalone components, Router, HttpClient, interceptor, guards) |
| Hosting | Vercel (frontend), Railway (backend), [MYSQL HOST] (database) |

## Features

- Employee management (create, list)
- Attendance (Present/Absent, history per employee, overtime hours)
- Leave (apply, approve/reject)
- Payroll calculation with a full salary breakdown
- **Extra feature 1: Overtime** (paid at 1.5x hourly rate)
- **Extra feature 2: Role-based access** (ADMIN and EMPLOYEE, JWT)

## Architecture

```
Angular (Vercel)  -->  REST API (Spring Boot, Railway)  -->  MySQL
 pages, forms          Controller -> Service -> Repository
```

Backend layers:

| Layer | Responsibility |
|---|---|
| controller | HTTP only: receive request, validate, return response |
| service | Business logic (payroll, duplicate checks, leave rules) |
| repository | Database access (Spring Data JPA) |
| entity | Database table mapping |
| dto | Request/response objects, so entities are never exposed |
| exception | Custom exceptions and one global handler returning a uniform JSON error |
| security | JWT filter, token service, own-data access check |
| config | CORS, security rules, payroll constants |

### Architecture decisions

- **DTOs instead of entities in the API:** clients cannot set ids or internal fields, and the schema can change without breaking the API.
- **Flyway migrations, `ddl-auto=validate`:** the schema is versioned in SQL, reproducible on any server, and Hibernate only checks that entities match.
- **Payroll is calculated on demand, not stored:** it is always consistent with attendance and cannot go out of sync. A `payroll_record` table can be added later for history.
- **`BigDecimal` for money:** exact arithmetic. Values are calculated with 10 decimals and rounded once at the end (2 decimals, HALF_UP).
- **No hardcoding:** database credentials, JWT secret, admin password, CORS origin and payroll constants (days in month, hours per day, overtime multiplier) come from environment variables or `application.properties`.
- **Duplicate attendance is blocked in two places:** a service check for a friendly message, and a database unique constraint on (employee_id, attendance_date) for race conditions.
- **Stateless JWT authentication:** no server session. Roles are enforced on the server (`hasRole` rules plus an own-data check), not only in the UI.
- **Global exception handler:** consistent error JSON (400, 401, 403, 404, 409) and no stack traces leaked to clients.

## Database Schema

```
employee (1) ----< attendance (many)
employee (1) ----< leave_request (many)
employee (1) ----0..1 app_user
```

| Table | Columns |
|---|---|
| employee | id (PK), name, role (WFH/OFFICE/ONSITE), salary_type (MONTHLY/DAILY), salary_amount DECIMAL(12,2), created_at |
| attendance | id (PK), employee_id (FK), attendance_date, status (PRESENT/ABSENT), overtime_hours DECIMAL(4,2). UNIQUE (employee_id, attendance_date) |
| leave_request | id (PK), employee_id (FK), start_date, end_date, reason, status (PENDING/APPROVED/REJECTED), created_at |
| app_user | id (PK), username (UNIQUE), password (BCrypt hash), role (ADMIN/EMPLOYEE), employee_id (FK, nullable), created_at |

Migrations are in `src/main/resources/db/migration`.

## Payroll Logic

```
Monthly: ratePerDay = monthlySalary / 30
Daily:   ratePerDay = dailyWage
basePay = ratePerDay x presentDays

Overtime (extra feature):
hourlyRate  = ratePerDay / 8
overtimePay = hourlyRate x overtimeHours x 1.5
grossSalary = basePay + overtimePay
```

Example: monthly salary 30000, 2 present days, 4 overtime hours gives rate per day 1000.00, base pay 2000.00, overtime pay 750.00, gross 2750.00.

The 30, 8 and 1.5 come from `payroll.days-in-month`, `payroll.hours-per-day` and `payroll.overtime-multiplier`.

## API Endpoints

| Method | URL | Access | Description |
|---|---|---|---|
| POST | /api/auth/register | Public | Employee registers (linked to an existing employee id) |
| POST | /api/auth/login | Public | Login, returns JWT |
| POST | /api/employees | Admin | Create employee |
| GET | /api/employees | Admin | List employees |
| POST | /api/attendance | Admin | Mark attendance |
| GET | /api/attendance/employee/{id} | Admin, own employee | Attendance history |
| POST | /api/leaves | Employee | Apply leave |
| GET | /api/leaves | Admin | All leave requests |
| GET | /api/leaves/employee/{id} | Admin, own employee | Leaves of one employee |
| PATCH | /api/leaves/{id}/status | Admin | Approve or reject |
| GET | /api/payroll/{id}?month=YYYY-MM | Admin, own employee | Salary breakdown |
| GET | /actuator/health | Public | Health check |

Error format:

```json
{ "timestamp": "...", "status": 400, "error": "Bad Request",
  "message": "Validation failed", "fieldErrors": { "name": "Name is required" } }
```

Status codes: 400 validation or rule broken, 401 not authenticated, 403 not allowed, 404 not found, 409 duplicate.

## Roles

- **ADMIN:** all features.
- **EMPLOYEE:** registers and logs in, applies for leave, views only their own leaves, attendance and payroll.
- Separate login pages: `/login` (employee) and `/admin-login` (admin).

## Setup (local)

Requirements: Java, Maven, MySQL, Node.js, Angular CLI.

**Backend**

1. Create the database: `CREATE DATABASE payroll_db;`
2. Set environment variables:

| Variable | Purpose | Default |
|---|---|---|
| DB_URL | JDBC URL | jdbc:mysql://localhost:3306/payroll_db |
| DB_USERNAME | DB user | root |
| DB_PASSWORD | DB password | none, required |
| ADMIN_USERNAME | First admin username | admin |
| ADMIN_PASSWORD | First admin password | none, required |
| JWT_SECRET | JWT signing key, at least 32 characters | none, required |
| JWT_EXPIRATION_MINUTES | Token lifetime | 60 |
| CORS_ALLOWED_ORIGINS | Allowed frontend origin | http://localhost:4200 |
| PORT | Server port | 8080 |

3. Run `./mvnw spring-boot:run`. Flyway creates the tables and the admin user is created on first start.

**Frontend**

1. `npm install`
2. Set `apiUrl` in `src/environments/environment.ts`.
3. `ng serve`, then open http://localhost:4200

## Assumptions

- A month is always 30 days, as the formula in the brief specifies.
- Only PRESENT days are paid. Absent and unmarked days are unpaid.
- Approved leave is unpaid, because the brief pays only for present days.
- Overtime applies only to PRESENT records, at 1.5x the hourly rate (rate per day / 8 hours).
- Attendance cannot be marked for a future date, or twice for the same employee and day.
- Employees self-register by choosing their employee id. The employee record must already exist.

## Known Limitations and Future Improvements

- Registration by employee id can be claimed by anyone who knows the id. Production fix: admin-issued invite codes.
- No automated tests yet (unit tests for payroll and integration tests for APIs are the next step).
- No pagination on list endpoints.
- JWT is stored in localStorage and there is no refresh token. Production would use short-lived tokens with refresh and httpOnly cookies.
- Payroll history is not stored, and half-day attendance and paid leave are not supported.
- Free hosting tiers may sleep when idle.