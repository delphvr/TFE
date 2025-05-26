## What This Project Does

This repository contains the code for a mobile application designed to help organize show rehearsals. It centralizes all relevant information for participants and allows organizers to automatically create an optimized rehearsal schedule. Each participant enter their availability, and organizers enter rehearsal requirements such as duration and the order of rehearsals to be respected (if any). The generation of the schedule will maximize participant availability at each rehearsal while respecting all the given constraints.

## User Manual

The user manual is available in French: [User Manual (FR)](./user_manual.md).  
Note: The application is currently only available in French.

## Configuration

Before running the application, you must set up a few configuration files.

1. [Frontend Configuration](#frontend-configuration)
2. [Backend Configuration](#backend-configuration)
    1. [Main Application](#main-application)
    2. [Test Environment](#test-environment)

### Frontend Configuration
The frontend is located in the `calendar_app` directory.
You need to create a `.env` file inside the `calendar_app/lib` directory containing the backend URL followed by `/api` like this:
```
API_BASE_URL=http://{ip_adress}:{port}/api
```
Replace `{ip_address}` and `{port}` with the actual address and port where your backend is running.

### Backend Configuration

You will need to add one file for the main application and two files for the test environment.

#### Main Application

Create a file named `application.properties` inside the `backend/src/main/resources` directory with the following content:

```properties
# DataSource settings
spring.datasource.url=jdbc:postgresql://{ip_address}:{port}/{your_db}  # TODO: Replace with your actual database URL
spring.datasource.username={your_username} # TODO: Replace with your database username
spring.datasource.password={your_password} # TODO: Replace with your database password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate properties
spring.jpa.database-platform = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql = false
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.implicit-strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true
```

Be sure to replace `{ip_address}`, `{port}`, `{your_db}`, `{your_username}`, and `{your_password}` with the actual connection details for your PostgreSQL database.

#### Test Environment

**Important**: Use separate databases for testing.  Tests may delete data in these databases.

Create a resources folder inside the `backend/src/test` directory if it doesn’t exist, then add the following two files:

##### 1. application-test.properties
```properties
spring.config.activate.on-profile=test
logging.level.root=DEBUG

# DataSource settings
spring.datasource.url=jdbc:postgresql://{ip_address}:{port}/{your_test_db}     # TODO: Replace with your test database URL 
spring.datasource.username={your_username} # TODO: Replace with your test database username
spring.datasource.password={your_password} # TODO: Replace with your test database password

spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate properties
spring.jpa.database-platform = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql = false
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.implicit-strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.springframework=DEBUG
```
Be sure to replace `{ip_address}`, `{port}`, `{your_test_db}`, `{your_username}`, and `{your_password}` with the actual connection details for your PostgreSQL test database.

##### 2. application-calendar_cp_test.properties
```properties
spring.config.activate.on-profile=calendar_cp_test
logging.level.root=DEBUG

# DataSource settings
spring.datasource.url=jdbc:postgresql://{ip_address}:{port}/{your_cp_test_db} # TODO: Replace with your CP test database URL
spring.datasource.username={your_username} # TODO: Replace with your CP test database username
spring.datasource.password={your_password} # TODO: Replace with your CP test database password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate properties
spring.jpa.database-platform = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql = false
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.implicit-strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.springframework=DEBUG
```
Be sure to replace `{ip_address}`, `{port}`, `{your_cp_test_db}`, `{your_username}`, and `{your_password}` with the actual connection details for your PostgreSQL CP test database.

##### Test Data Setup
In the `backend/src` directory, you will find a file named `test_db.sql`, which contains test data used in the calendar_cp tests.
\
You can load it into your test database using the following command:
```bash
psql -U postgres -d {your_cp_test_db} -f test_db.sql
```
Replace `{your_cp_test_db}` with the name of the database configured in `application-calendar_cp_test.properties`.