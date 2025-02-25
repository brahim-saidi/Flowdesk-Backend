-- Create sequences for primary keys
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ticket_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE comment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE audit_seq START WITH 1 INCREMENT BY 1;

-- Users table
CREATE TABLE users (
    user_id NUMBER DEFAULT user_seq.NEXTVAL PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(100) NOT NULL, 
    email VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'IT_SUPPORT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Tickets table
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

-- Comments table
CREATE TABLE comments (
    comment_id NUMBER DEFAULT comment_seq.NEXTVAL PRIMARY KEY,
    ticket_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    content CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Audit log table
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

-- Indexes for better performance
CREATE INDEX idx_ticket_status ON tickets(status);
CREATE INDEX idx_ticket_created_by ON tickets(created_by);
CREATE INDEX idx_ticket_assigned_to ON tickets(assigned_to);
CREATE INDEX idx_comment_ticket ON comments(ticket_id);
CREATE INDEX idx_audit_ticket ON audit_log(ticket_id);

-- Create initial admin user
INSERT INTO users (username, password, email, role)
VALUES ('emp', 'emp', 'emp@company.com', 'EMPLOYEE');

INSERT INTO users (username, password, email, role)
VALUES ('admin1', 'admin1', 'admin@company.com', 'IT_SUPPORT');


INSERT INTO users (username, password, email, role)
VALUES ('emp1', 'emp1', 'emp@company.com', 'EMPLOYEE');

INSERT INTO users (username, password, email, role)
VALUES ('admin2', 'admin2', 'admin@company.com', 'IT_SUPPORT');