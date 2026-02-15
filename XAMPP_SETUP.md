# Alzheimer Detection System - Alternative Setup with XAMPP

## If You Don't Have Docker

You can use **XAMPP** instead, which includes MySQL and phpMyAdmin pre-installed.

### Installation Steps

1. **Download XAMPP**
   - Go to: https://www.apachefriends.org/
   - Download the Windows installer (with MySQL)
   - Run the installer

2. **Start XAMPP Services**
   - Open XAMPP Control Panel
   - Click "Start" for Apache and MySQL
   - Wait until both show as running (green)

3. **Access phpMyAdmin**
   - Open: http://localhost/phpmyadmin
   - Username: root
   - Password: (leave blank by default)

### Create the Database

1. In phpMyAdmin:
   - Click "New" or "Create database"
   - Database name: `alzheimer_db`
   - Collation: `utf8mb4_unicode_ci`
   - Click "Create"

2. Select the `alzheimer_db` database

3. Go to "SQL" tab and run this query:

```sql
-- Create users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create mmse_tests table
CREATE TABLE mmse_tests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_name VARCHAR(255),
    orientation_score INT NOT NULL,
    registration_score INT NOT NULL,
    attention_score INT NOT NULL,
    recall_score INT NOT NULL,
    language_score INT NOT NULL,
    total_score INT NOT NULL,
    interpretation VARCHAR(255),
    test_date DATE,
    notes VARCHAR(255)
);

-- Insert sample users
INSERT INTO users (email, first_name, last_name, password, role) VALUES
('admin@alzheimer.com', 'Admin', 'User', 'admin123', 'ADMIN'),
('doctor@alzheimer.com', 'Dr', 'Smith', 'doctor123', 'DOCTOR'),
('patient1@alzheimer.com', 'John', 'Doe', 'patient123', 'USER'),
('patient2@alzheimer.com', 'Jane', 'Smith', 'patient123', 'USER');

-- Insert sample MMSE tests
INSERT INTO mmse_tests (patient_name, orientation_score, registration_score, attention_score, recall_score, language_score, total_score, interpretation, test_date, notes)
VALUES
('John Doe', 10, 3, 5, 3, 9, 30, 'Normal cognition', '2026-01-15', 'Initial assessment'),
('Jane Smith', 8, 3, 5, 2, 8, 26, 'Normal cognition', '2026-01-20', 'Follow-up test');
```

4. Click "Go" to execute

### Update Backend Configuration

Edit `Backend/src/main/resources/application.properties`:

```properties
spring.application.name=backend

# MySQL Database Configuration for XAMPP
spring.datasource.url=jdbc:mysql://localhost:3306/alzheimer_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
```

### Start the Backend

```powershell
cd C:\Alzheimer-Detection-System\Backend
.\mvnw.cmd spring-boot:run
```

### Access Services

- **phpMyAdmin:** http://localhost/phpmyadmin
- **MySQL:** localhost:3306 (root user, no password)
- **Backend API:** http://localhost:8080
- **Admin Dashboard:** http://localhost:4200

---

Choose whichever setup method works best for you:
- **Docker + Docker Compose** (easiest, most modern)
- **XAMPP** (simpler, all-in-one)
