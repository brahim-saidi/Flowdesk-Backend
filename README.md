# IT Support Ticket System

A Java-based ticket management application that allows employees to report and track IT issues.

## Features

- **Ticket Management**: Create, view, and update IT support tickets
- **User Roles**: Separate interfaces for employees and IT support staff
- **Status Tracking**: Follow tickets through their lifecycle
- **Commenting System**: Add comments to tickets for better communication
- **Audit Logging**: Track all changes made to tickets
- **Search & Filter**: Find tickets by ID and status

## Technology Stack

- **Backend**: Java 17, Spring Boot, RESTful API
- **Database**: Oracle SQL
- **UI**: Java Swing with MigLayout
- **API Documentation**: OpenAPI/Swagger
- **Testing**: JUnit, Mockito
- **Containerization**: Docker

## System Requirements

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6 or higher (for development)
- Git (for cloning the repository)

## Quick Start

### Running with Docker

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
   - Swing Client: Run the JAR file in the `client/target` directory
   - API: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Default Users

Two default users are created during initialization:

- Employee:
  - Username: `emp`
  - Password: `emp`
  - Role: `EMPLOYEE`

- IT Support:
  - Username: `admin1`
  - Password: `admin1`
  - Role: `IT_SUPPORT`

## Development Setup

### Backend

1. Configure Oracle database connection in `src/main/resources/application.properties`

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   java -jar target/ticket-system-1.0.0.jar
   ```

### Client

1. Build the Swing client:
   ```bash
   cd client
   mvn clean package
   ```

2. Run the client:
   ```bash
   java -jar target/ticket-client-1.0.0-jar-with-dependencies.jar
   ```

## Database Schema

The application uses an Oracle SQL database with the following structure:

- **users**: Stores user information and credentials
- **tickets**: Main ticket information
- **comments**: Comments added to tickets
- **audit_log**: Record of all changes to tickets

The complete schema is available in `src/main/resources/data.sql`.

## API Documentation

The REST API is documented using OpenAPI/Swagger. You can access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

A comprehensive API documentation is also available in the `API-DOCUMENTATION.md` file.

## Docker Configuration

The application is containerized using Docker with the following components:

- **Backend**: Spring Boot application
- **Database**: Oracle XE 18c

The Docker setup is defined in `docker-compose.yml` and includes:

- Volume mapping for database persistence
- Environment variable configuration
- Port mappings for the application and database

## Project Structure

```
ticket-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hahnSoftware/
│   │   │           └── ticket/
│   │   │               ├── config/          # Configuration classes
│   │   │               ├── controller/      # REST endpoints
│   │   │               ├── entity/          # Domain models
│   │   │               ├── repository/      # Data access layer
│   │   │               ├── service/         # Business logic
│   │   │               └── TicketApplication.java
│   │   └── resources/
│   │       ├── application.properties       # Application configuration
│   │       └── data.sql                    # Database schema
│   └── test/                               # Unit and integration tests
├── client/                                 # Swing client application
├── docker/                                 # Docker configuration files
├── API-DOCUMENTATION.md                    # API documentation
├── docker-compose.yml                      # Docker Compose configuration
├── Dockerfile                              # Docker image definition
└── pom.xml                                 # Maven configuration
```

## Testing

The application includes comprehensive unit and integration tests. Run the tests with:

```bash
mvn test
```

