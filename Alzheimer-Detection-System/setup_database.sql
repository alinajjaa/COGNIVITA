-- Create the database
CREATE DATABASE IF NOT EXISTS alzheimer_db;
USE alzheimer_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
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
CREATE TABLE IF NOT EXISTS mmse_tests (
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

-- Create medical_records table (Complete patient clinical file)
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    age INT,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    education_level VARCHAR(100),
    family_history ENUM('Yes', 'No') DEFAULT 'No',
    risk_factors TEXT,
    current_symptoms TEXT,
    diagnosis_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
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
('Jane Smith', 8, 3, 5, 2, 8, 26, 'Normal cognition', '2026-01-20', 'Follow-up test'),
('John Doe', 9, 3, 4, 2, 8, 26, 'Normal cognition', '2026-02-01', 'Second follow-up');

-- Insert sample medical records
INSERT INTO medical_records (user_id, age, gender, education_level, family_history, risk_factors, current_symptoms, diagnosis_notes)
VALUES
(3, 68, 'Male', 'Bachelor Degree', 'Yes', 'Hypertension, Diabetes Type 2, Sedentary lifestyle', 'Mild memory lapses, occasional confusion with dates', 'Patient shows early signs of cognitive decline. Recommend regular MMSE monitoring and lifestyle modifications.'),
(4, 72, 'Female', 'High School', 'No', 'History of head trauma, Smoking (former)', 'Difficulty remembering recent events, word-finding problems', 'MCI suspected. Brain MRI scheduled. Family education provided about disease progression.');
