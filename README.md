# IT Support Ticket System — Backend API

Spring Boot REST API for the IT support ticket system. The **web UI is a separate [React](https://react.dev/) application** hosted on GitHub; this repository contains only the backend.

## Repositories

| Part | Stack | Repository |
|------|--------|------------|
| **Backend** (this repo) | Java 17, Spring Boot 3 | [github.com/brahim-saidi/tickets](https://github.com/brahim-saidi/tickets) |
| **Frontend** | React | *Add your React app’s GitHub URL here* (e.g. `https://github.com/<user>/<repo>`) |

Clone the React project separately, install dependencies (`npm install` / `pnpm install` / `yarn`), and point the client at this API (see [Connecting the React app](#connecting-the-react-app)).

## Features

- **Tickets**: Create, read, update status; **paginated** list with **sort**; filters by **title**, **created date range**, **assignee** (IT support)
- **Roles**: Employees see tickets they created or are assigned to; IT support sees all tickets
- **Comments** and **audit logging** on tickets
- **Admin (IT support)**: Create users, **paginated** user list, **enable/disable** accounts, **password reset**
- **Auth**: JWT login (`POST /api/auth/login`), **`GET /api/auth/me`** for the current user (for React)
- **Errors**: Consistent JSON body: `code`, `message`, `fieldErrors` for common status codes (400 / 401 / 403 / 404 / 409)
- **Ops**: **Flyway** migrations, **Spring Boot Actuator** health (including **database**) for deployments

## Technology stack

- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring Security, JWT
- **Database**: PostgreSQL
- **Migrations**: Flyway — `src/main/resources/db/migration/`
- **API docs**: OpenAPI / Swagger UI
- **Testing**: JUnit 5, Mockito
- **Frontend** (separate repo): React (see table above)

## Requirements

- Java 17+
- Maven 3.6+
- Docker (optional, for local PostgreSQL)

## Quick start — backend

1. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

   Uses `docker-compose.yml` (Postgres 16, database `ticket`, user/password `postgres`).

2. Run the API (Flyway applies schema and seed data on startup):

   ```bash
   mvn spring-boot:run
   ```

3. **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
4. **Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

Configuration: `src/main/resources/application.properties` (override with `application-local.properties` or environment variables for production).

### Default users (Flyway seed)

- Employee: `emp` / `emp`
- IT support: `admin1` / `admin1`

## Connecting the React app

1. In the **React** repository, set the API base URL to this backend (commonly `http://localhost:8080`). Use whatever mechanism your template uses, for example:
   - **Vite**: `VITE_API_URL` or similar in `.env`
   - **Create React App**: `REACT_APP_API_URL` in `.env`
2. After login, send the JWT on requests: `Authorization: Bearer <token>`.
3. **CORS** is configured for common dev servers in `application.properties` (`app.cors.allowed-origins`), including `http://localhost:5173` (Vite) and `http://localhost:3000` (CRA). Add your origin if it differs.

## Build and test

```bash
mvn clean verify
```

## Database schema

Schema changes are managed with **Flyway** only (`src/main/resources/db/migration/`). Do not hand-edit production schema outside migrations.

## Project layout

```
tickets/
├── src/main/java/com/hahnSoftware/ticket/
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/              # Flyway
├── docker-compose.yml             # Local PostgreSQL
└── pom.xml
```

The React UI lives in its **own** GitHub repository; it is not part of this Maven tree.
