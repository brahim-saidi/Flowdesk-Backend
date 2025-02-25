#!/bin/bash
set -e

# Wait for Oracle to be ready
echo "Waiting for Oracle database to be ready..."
sleep 30

# Run SQL script
echo "Executing SQL initialization script..."
sqlplus system/pass123@//localhost:1521/XEPDB1 @/docker-entrypoint-initdb.d/startup/data.sql

echo "Database initialization completed."