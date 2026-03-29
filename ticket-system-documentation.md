# IT Support Ticket System
### API & System Documentation
#### Version 1.0.0
#### Hahn Software

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Architecture](#2-system-architecture)
3. [API Reference](#3-api-reference)
4. [Data Models](#4-data-models)
5. [Authentication](#5-authentication)
6. [Database Schema](#6-database-schema)
7. [Error Handling](#7-error-handling)
8. [Setup & Deployment](#8-setup--deployment)
9. [Client Application](#9-client-application)
10. [Appendix](#10-appendix)

---

## 1. Introduction

The IT Support Ticket System is a comprehensive solution designed to streamline and manage IT support requests within an organization. This documentation provides detailed information about the system's architecture, API endpoints, data models, and deployment procedures.

### 1.1 Purpose

The system allows employees to report and track IT issues while enabling IT support staff to efficiently manage these tickets through their lifecycle.

### 1.2 Key Features

- Ticket creation and management
- Status tracking through defined workflow
- Role-based access control
- Commenting system
- Comprehensive audit logging
- Search and filtering capabilities

---

## 2. System Architecture

### 2.1 Technology Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, JWT
- **Database**: PostgreSQL
- **Schema migrations**: Flyway (`src/main/resources/db/migration/`)
- **API documentation**: OpenAPI/Swagger
- **Testing**: JUnit, Mockito
- **Operations**: Docker Compose for local PostgreSQL; Spring Boot Actuator (health, including DB)

### 2.2 Component diagram

- **Spring Boot REST API**: Business logic and HTTP layer
- **PostgreSQL**: Persistent storage
- **Optional front ends**: Any HTTP client (e.g. React) calling the REST API
- **Docker Compose**: Local PostgreSQL only (see `docker-compose.yml`)

### 2.3 Authentication design

JWT bearer tokens are issued on `POST /api/auth/login`. Protected routes require `Authorization: Bearer <token>`. Roles (`EMPLOYEE`, `IT_SUPPORT`) are enforced with Spring Security method security where applicable.

---

## 3. API Reference

### 3.1 Base URL

All API endpoints are relative to: `http://localhost:8080/api`

### 3.2 User Management

#### 3.2.1 Login

```
POST /auth/login
```
(relative to base URL `http://localhost:8080/api`, i.e. `POST /api/auth/login`)

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "userId": "number",
  "username": "string",
  "role": "string",
  "authenticated": "boolean"
}
```

**Status Codes:**
- 200 OK: Authentication successful
- 401 Unauthorized: Invalid credentials

### 3.3 Ticket Management

#### 3.3.1 Create a Ticket

```
POST /tickets
```

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "priority": "LOW|MEDIUM|HIGH",
  "category": "NETWORK|HARDWARE|SOFTWARE|OTHER",
  "status": "NEW",
  "createdByUserId": "number"
}
```

**Response:**
```json
{
  "ticketId": "number",
  "title": "string",
  "description": "string",
  "priority": "string",
  "category": "string",
  "status": "string", 
  "createdAt": "timestamp",
  "createdBy": {
    "userId": "number",
    "username": "string"
  }
}
```

**Status Codes:**
- 200 OK: Ticket created successfully
- 400 Bad Request: Invalid input

#### 3.3.2 Get All Tickets

```
GET /tickets
```

**Response:**
```json
[
  {
    "ticketId": "number",
    "title": "string",
    "description": "string",
    "priority": "string",
    "category": "string",
    "status": "string",
    "createdAt": "timestamp",
    "createdBy": {
      "userId": "number",
      "username": "string"
    },
    "assignedUser": {
      "userId": "number",
      "username": "string"
    },
    "comments": [...]
  }
]
```

**Status Codes:**
- 200 OK: Retrieved all tickets

#### 3.3.3 Get Ticket by ID

```
GET /tickets/{ticketId}
```

**Path Parameters:**
- `ticketId`: ID of the ticket to retrieve

**Response:**
```json
{
  "ticketId": "number",
  "title": "string",
  "description": "string",
  "priority": "string",
  "category": "string",
  "status": "string",
  "createdAt": "timestamp",
  "createdBy": {
    "userId": "number",
    "username": "string"
  },
  "assignedUser": {
    "userId": "number",
    "username": "string"
  },
  "comments": [...]
}
```

**Status Codes:**
- 200 OK: Found the ticket
- 404 Not Found: Ticket not found

#### 3.3.4 Get Tickets by Status

```
GET /tickets/status/{status}
```

**Path Parameters:**
- `status`: Filter tickets by status (NEW, IN_PROGRESS, RESOLVED)

**Response:**
```json
[
  {
    "ticketId": "number",
    "title": "string",
    "description": "string",
    "priority": "string",
    "category": "string",
    "status": "string",
    "createdAt": "timestamp",
    ...
  }
]
```

**Status Codes:**
- 200 OK: Found tickets with specified status
- 400 Bad Request: Invalid status value

#### 3.3.5 Update Ticket Status

```
PUT /tickets/{ticketId}/status
```

**Path Parameters:**
- `ticketId`: ID of the ticket to update

**Query Parameters:**
- `newStatus`: New status value (NEW, IN_PROGRESS, RESOLVED)

**Response:**
```json
{
  "ticketId": "number",
  "title": "string",
  "description": "string",
  "priority": "string",
  "category": "string",
  "status": "string",
  "createdAt": "timestamp",
  ...
}
```

**Status Codes:**
- 200 OK: Status updated successfully
- 404 Not Found: Ticket not found
- 400 Bad Request: Invalid status value

### 3.4 Comment Management

#### 3.4.1 Add Comment to Ticket

```
POST /comments
```

**Request Body:**
```json
{
  "ticketId": "number",
  "userId": "number",
  "content": "string"
}
```

**Response:**
```json
{
  "commentId": "number",
  "content": "string",
  "createdAt": "timestamp",
  "user": {
    "userId": "number",
    "username": "string"
  },
  "ticket": {
    "ticketId": "number"
  }
}
```

**Status Codes:**
- 200 OK: Comment added successfully
- 400 Bad Request: Invalid input

#### 3.4.2 Get Comments for a Ticket

```
GET /comments/ticket/{ticketId}
```

**Path Parameters:**
- `ticketId`: ID of the ticket to get comments for

**Response:**
```json
[
  {
    "commentId": "number",
    "content": "string",
    "createdAt": "timestamp",
    "user": {
      "userId": "number",
      "username": "string"
    }
  }
]
```

**Status Codes:**
- 200 OK: Retrieved comments successfully
- 404 Not Found: Ticket not found

### 3.5 Audit Log

#### 3.5.1 Get Audit Logs for a Ticket

```
GET /audit/ticket/{ticketId}
```

**Path Parameters:**
- `ticketId`: ID of the ticket to get audit logs for

**Response:**
```json
[
  {
    "auditId": "number",
    "action": "string",
    "oldValue": "string",
    "newValue": "string",
    "createdAt": "timestamp",
    "user": {
      "userId": "number",
      "username": "string"
    }
  }
]
```

**Status Codes:**
- 200 OK: Retrieved audit logs successfully
- 404 Not Found: Ticket not found

---

## 4. Data Models

### 4.1 Ticket

```json
{
  "ticketId": "number",
  "title": "string",
  "description": "string",
  "priority": "LOW|MEDIUM|HIGH",
  "category": "NETWORK|HARDWARE|SOFTWARE|OTHER",
  "status": "NEW|IN_PROGRESS|RESOLVED",
  "createdAt": "timestamp",
  "createdBy": {
    "userId": "number",
    "username": "string"
  },
  "assignedUser": {
    "userId": "number",
    "username": "string"
  },
  "comments": [
    {
      "commentId": "number",
      "content": "string",
      "createdAt": "timestamp",
      "user": {
        "userId": "number",
        "username": "string"
      }
    }
  ],
  "auditLogs": [
    {
      "auditId": "number",
      "action": "string",
      "oldValue": "string",
      "newValue": "string",
      "createdAt": "timestamp",
      "user": {
        "userId": "number",
        "username": "string"
      }
    }
  ]
}
```

### 4.2 User

```json
{
  "userId": "number",
  "username": "string",
  "email": "string",
  "role": "EMPLOYEE|IT_SUPPORT",
  "enabled": "boolean",
  "createdAt": "timestamp",
  "lastLogin": "timestamp"
}
```

### 4.3 Comment

```json
{
  "commentId": "number",
  "content": "string",
  "createdAt": "timestamp",
  "user": {
    "userId": "number",
    "username": "string"
  },
  "ticket": {
    "ticketId": "number"
  }
}
```

### 4.4 AuditLog

```json
{
  "auditId": "number",
  "action": "string",
  "oldValue": "string",
  "newValue": "string",
  "createdAt": "timestamp",
  "user": {
    "userId": "number"
  },
  "ticket": {
    "ticketId": "number"
  }
}
```

---

## 5. Authentication

Users authenticate with `POST /api/auth/login` and receive a JWT. Include `Authorization: Bearer <token>` on subsequent requests. Disabled accounts cannot log in or call `GET /api/auth/me`.

The system has two roles:
- EMPLOYEE: Can create and view their own tickets
- IT_SUPPORT: Can view all tickets, change statuses, and add comments

### 5.1 Default Users

The following default users are created during initialization:

**IT Support Users:**
- Username: `admin1` / Password: `admin1` / Role: `IT_SUPPORT`
- Username: `admin2` / Password: `admin2` / Role: `IT_SUPPORT`


**Employee Users:**
- Username: `emp` / Password: `emp` / Role: `EMPLOYEE`
- Username: `emp1` / Password: `emp1` / Role: `EMPLOYEE`

---

## 6. Database schema

The canonical schema is maintained by **Flyway** in `src/main/resources/db/migration/` (PostgreSQL). The following is a simplified reference.

### 6.1 Users table
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'IT_SUPPORT')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

### 6.2 Tickets table
```sql
CREATE TABLE tickets (
    ticket_id BIGINT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    category VARCHAR(20) NOT NULL CHECK (category IN ('NETWORK', 'HARDWARE', 'SOFTWARE', 'OTHER')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('NEW', 'IN_PROGRESS', 'RESOLVED')),
    created_by BIGINT NOT NULL,
    assigned_to BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_ticket_creator FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assigned_to) REFERENCES users(user_id)
);
```

### 6.3 Comments table
```sql
CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### 6.4 Audit log table
```sql
CREATE TABLE audit_log (
    audit_id BIGINT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value VARCHAR(100),
    new_value VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### 6.5 Database Entity Relationship Diagram

The database follows a relational structure with the following relationships:
- A User can create many Tickets (1:N)
- A User can be assigned to many Tickets (1:N)
- A Ticket belongs to one Creator User (N:1)
- A Ticket can be assigned to one Support User (N:1)
- A Ticket can have many Comments (1:N)
- A Ticket can have many Audit Logs (1:N)
- A User can create many Comments (1:N)
- A User can generate many Audit Logs (1:N)

---

## 7. Error handling

Typical HTTP status codes:

- **400** Bad Request: validation or invalid parameters
- **401** Unauthorized: missing or invalid JWT (also failed login)
- **403** Forbidden: authenticated but not allowed
- **404** Not Found: missing resource (including deliberately hidden tickets for unauthorized users)
- **409** Conflict: e.g. duplicate username on admin create user

JSON error body shape (consistent across `@RestControllerAdvice` and security entry points where applicable):

```json
{
  "code": "STRING_CODE",
  "message": "Human-readable summary",
  "fieldErrors": [
    { "field": "fieldName", "message": "reason" }
  ]
}
```

`fieldErrors` is an empty array when not a field-level validation error.

---

## 8. Setup & Deployment

### 8.1 Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6 or higher (for development)
- Git (for cloning the repository)

### 8.2 Local database (Docker Compose)

1. From the project root:
   ```bash
   docker compose up -d
   ```
   This starts PostgreSQL (see `docker-compose.yml`: database `ticket`, user/password `postgres`).

2. Run the Spring Boot application (Flyway runs on startup):
   ```bash
   mvn spring-boot:run
   ```

3. Useful URLs:
   - API base: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - Liveness/readiness-style health: `http://localhost:8080/actuator/health`

### 8.3 Development setup

1. Point `spring.datasource.*` in `src/main/resources/application.properties` at your PostgreSQL instance (or use the Docker Compose service on `localhost:5432`).

2. Build and test:
   ```bash
   mvn clean verify
   ```

3. Run:
   ```bash
   mvn spring-boot:run
   ```

---

## 9. Client applications

Any HTTP client can integrate with the API (e.g. a React SPA). Use the OpenAPI/Swagger UI for request and response shapes. Typical flow: login → store JWT → attach `Authorization` header → call ticket and admin endpoints as allowed by role.

---

## 10. Appendix

### 10.1 API endpoints summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/login | Authenticate; returns JWT |
| GET | /api/auth/me | Current user from JWT |
| POST | /api/tickets | Create ticket |
| GET | /api/tickets | Paged ticket search (`title`, `createdFrom`, `createdTo`, `assigneeUserId`, standard `page`, `size`, `sort`) |
| GET | /api/tickets/{ticketId} | Ticket by ID |
| GET | /api/tickets/status/{status} | Tickets by status |
| PUT | /api/tickets/{ticketId}/status | Update ticket status |
| GET | /api/admin/users | Paged user list (IT support) |
| POST | /api/admin/users | Create user (IT support) |
| PATCH | /api/admin/users/{userId}/enabled | Enable/disable user (IT support) |
| POST | /api/admin/users/{userId}/reset-password | Reset password (IT support) |
| POST | /api/comments | Add comment |
| GET | /api/comments/ticket/{ticketId} | Comments for ticket |
| GET | /api/audit-logs/ticket/{ticketId} | Audit logs for ticket |
| GET | /actuator/health | Liveness/readiness (public) |

### 10.2 OpenAPI Documentation

The API is documented using OpenAPI 3.0 specification and can be accessed at:

```
http://localhost:8080/swagger-ui.html
```

---

