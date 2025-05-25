-- Create the database
CREATE DATABASE IF NOT EXISTS room_booking;
USE room_booking;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS reservation_equipment;
DROP TABLE IF EXISTS reservation_participants;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS profiles;

-- Create profiles table (updated structure)
CREATE TABLE profiles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create users table (updated structure)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    profile_id INT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE SET NULL
);

-- Create rooms table (updated structure)
CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(100),
    type VARCHAR(50),
    features TEXT,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create equipment table (updated structure)
CREATE TABLE equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    description TEXT,
    is_available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create reservations table (updated structure)
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    subject VARCHAR(200),
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    admin_comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Create reservation_equipment junction table
CREATE TABLE reservation_equipment (
    reservation_id INT,
    equipment_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reservation_id, equipment_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Create reservation_participants table (updated structure)
CREATE TABLE reservation_participants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- Insert sample data

-- Insert sample profiles
INSERT INTO profiles (first_name, last_name, email, phone, department, description) VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', 'IT', 'Regular employee with basic room booking privileges'),
('Jane', 'Smith', 'jane.smith@example.com', '0987654321', 'HR', 'Department manager with extended booking privileges'),
('Admin', 'User', 'admin@example.com', '1122334455', 'Administration', 'System administrator with full privileges');

-- Insert sample users (password is 'admin123' hashed with BCrypt)
INSERT INTO users (username, password_hash, email, role, profile_id) VALUES
('johndoe', '$2a$10$xLRxIEjD4hMHAjBY8qKyqeWQzxJFy.Pik4tKhsZ3LJJIRNtKQdKOK', 'john.doe@example.com', 'USER', 1),
('janesmith', '$2a$10$xLRxIEjD4hMHAjBY8qKyqeWQzxJFy.Pik4tKhsZ3LJJIRNtKQdKOK', 'jane.smith@example.com', 'USER', 2),
('admin', '$2a$10$xLRxIEjD4hMHAjBY8qKyqeWQzxJFy.Pik4tKhsZ3LJJIRNtKQdKOK', 'admin@example.com', 'ADMIN', 3);

-- Insert sample rooms
INSERT INTO rooms (name, capacity, location, type, features, description) VALUES
('Conference Room A', 20, 'Building A - Floor 1', 'CONFERENCE', 'Projector, Whiteboard, Video conferencing', 'Large conference room with modern amenities'),
('Meeting Room 1', 8, 'Building A - Floor 2', 'MEETING', 'TV Screen, Whiteboard', 'Small meeting room for team discussions'),
('Board Room', 12, 'Building B - Floor 3', 'BOARD', 'Video conferencing, Large display, Executive chairs', 'Executive board room with premium facilities'),
('Training Room', 30, 'Building C - Floor 1', 'TRAINING', 'Multiple projectors, Sound system, Breakout area', 'Large training room with breakout space');

-- Insert sample equipment
INSERT INTO equipment (name, type, description) VALUES
('Projector P1000', 'PROJECTOR', 'Full HD Projector with HDMI input'),
('Conference Phone CP200', 'PHONE', 'Conference phone with noise cancellation'),
('Laptop L1', 'COMPUTER', 'Presentation laptop with Office Suite'),
('Whiteboard Kit', 'ACCESSORIES', 'Markers, eraser, and magnets');

-- Create indexes for better performance
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_room ON reservations(room_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_time ON reservations(start_time, end_time);
CREATE INDEX idx_equipment_available ON equipment(is_available);
CREATE INDEX idx_rooms_active ON rooms(is_active);

-- Create helpful views

-- View for upcoming reservations
CREATE OR REPLACE VIEW upcoming_reservations AS
SELECT 
    r.id,
    r.subject,
    r.start_time,
    r.end_time,
    r.status,
    u.username,
    rm.name as room_name
FROM reservations r
JOIN users u ON r.user_id = u.id
JOIN rooms rm ON r.room_id = rm.id
WHERE r.start_time > NOW()
ORDER BY r.start_time;

-- View for room utilization
CREATE OR REPLACE VIEW room_utilization AS
SELECT 
    r.name as room_name,
    COUNT(res.id) as total_reservations,
    SUM(TIMESTAMPDIFF(HOUR, res.start_time, res.end_time)) as total_hours
FROM rooms r
LEFT JOIN reservations res ON r.id = res.room_id
WHERE res.status = 'APPROVED'
AND res.start_time > DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY r.id, r.name; 