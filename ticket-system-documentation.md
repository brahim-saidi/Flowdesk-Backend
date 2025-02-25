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

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: Oracle SQL
- **UI**: Java Swing with MigLayout
- **API Documentation**: OpenAPI/Swagger
- **Testing**: JUnit, Mockito
- **Containerization**: Docker

### 2.2 Component Diagram

The system consists of the following components:

- **Spring Boot REST API**: Handles all backend operations
- **Oracle Database**: Stores all system data
- **Java Swing Client**: Provides the user interface
- **Docker Containers**: Encapsulate the application and database

### 2.3 Authentication Design

This implementation uses a basic authentication approach with custom user management rather than Spring Security or JWT tokens, as these were not specified in the requirements. User roles and permissions are managed through custom logic.

---

## 3. API Reference

### 3.1 Base URL

All API endpoints are relative to: `http://localhost:8080/api`

### 3.2 User Management

#### 3.2.1 Login

```
POST /login
```

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

Authentication is handled via a simple username/password login mechanism. For simplicity, the system does not use Spring Security or JWT tokens as these were not specified in the requirements. Instead, a basic authentication approach is implemented to manage user roles and permissions.

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

## 6. Database Schema

The application uses an Oracle SQL database with the following schema:

### 6.1 Users Table
```sql
CREATE TABLE users (
    user_id NUMBER DEFAULT user_seq.NEXTVAL PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(100) NOT NULL, 
    email VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'IT_SUPPORT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

### 6.2 Tickets Table
```sql
CREATE TABLE tickets (
    ticket_id NUMBER DEFAULT ticket_seq.NEXTVAL PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    description CLOB NOT NULL,
    priority VARCHAR2(20) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    category VARCHAR2(20) NOT NULL CHECK (category IN ('NETWORK', 'HARDWARE', 'SOFTWARE', 'OTHER')),
    status VARCHAR2(20) NOT NULL CHECK (status IN ('NEW', 'IN_PROGRESS', 'RESOLVED')),
    created_by NUMBER NOT NULL,
    assigned_to NUMBER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_ticket_creator FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assigned_to) REFERENCES users(user_id)
);
```

### 6.3 Comments Table
```sql
CREATE TABLE comments (
    comment_id NUMBER DEFAULT comment_seq.NEXTVAL PRIMARY KEY,
    ticket_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    content CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### 6.4 Audit Log Table
```sql
CREATE TABLE audit_log (
    audit_id NUMBER DEFAULT audit_seq.NEXTVAL PRIMARY KEY,
    ticket_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    action VARCHAR2(50) NOT NULL,
    old_value VARCHAR2(100),
    new_value VARCHAR2(100),
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

## 7. Error Handling

The API returns appropriate HTTP status codes for different types of errors:

- 400 Bad Request: Invalid request parameters or validation errors
- 401 Unauthorized: Authentication issues
- 403 Forbidden: Authorization issues
- 404 Not Found: Resource not found
- 500 Internal Server Error: Server-side errors

Note: Since the application does not use Spring Security, authentication and authorization errors are handled through custom logic rather than through standard security filters.

Error responses include a message explaining the error:

```json
{
  "timestamp": "ISO date",
  "status": "number",
  "error": "string",
  "message": "string",
  "path": "string"
}
```

---

## 8. Setup & Deployment

### 8.1 Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6 or higher (for development)
- Git (for cloning the repository)

### 8.2 Docker Deployment

1. Clone the repository:
   ```bash
   git clone https://github.com/hahnSoftware/ticket-system.git
   cd ticket-system
   ```

2. Build and run the containers:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - API: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### 8.3 Development Setup

1. Configure Oracle database connection in `src/main/resources/application.properties`

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   java -jar target/ticket-system-1.0.0.jar
   ```

---

## 9. Client Application

### 9.1 Swing Client Features

The Java Swing client application provides:
- Separate interfaces for employees and IT support staff
- Ticket creation form
- Ticket listing with filtering options
- Comment management
- Status updates

### 9.2 Running the Client

1. Build the Swing client:
   ```bash
   cd client
   mvn clean package
   ```

2. Run the client:
   ```bash
   java -jar target/ticket-client-1.0.0-jar-with-dependencies.jar
   ```

---

## 10. Appendix

### 10.1 API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /login | Authenticate user |
| POST | /tickets | Create a new ticket |
| GET | /tickets | Get all tickets |
| GET | /tickets/{ticketId} | Get ticket by ID |
| GET | /tickets/status/{status} | Get tickets by status |
| PUT | /tickets/{ticketId}/status | Update ticket status |
| POST | /comments | Add comment to ticket |
| GET | /comments/ticket/{ticketId} | Get comments for a ticket |
| GET | /audit/ticket/{ticketId} | Get audit logs for a ticket |

### 10.2 OpenAPI Documentation

The API is documented using OpenAPI 3.0 specification and can be accessed at:

```
http://localhost:8080/swagger-ui.html
```

---

