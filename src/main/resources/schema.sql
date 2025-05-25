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

-- Create profiles table
CREATE TABLE profiles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create users table
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
select * from users;
-- Insert default admin user (password: admin123)

-- Create rooms table
CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(100),
    type VARCHAR(50),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create equipment table
CREATE TABLE equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    description TEXT,
    is_available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create reservations table
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

-- Create reservation_participants table
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
INSERT INTO profiles (first_name, last_name, email, phone, department) VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', 'IT'),
('Jane', 'Smith', 'jane.smith@example.com', '0987654321', 'HR'),
('Admin', 'User', 'admin@example.com', '1122334455', 'Administration');

-- Insert sample users (password is 'yahya123' hashed with BCrypt)
INSERT INTO users (username, password_hash, email, role, profile_id) VALUES
('john', '$2a$10$Be/WwQvP4GDU6eki4b0tQ.B4hBROlgMlBduwloFtEfuV8.sPURLoC', 'john.doe@example.com', 'USER', 1),
('jane', '$2a$10$Be/WwQvP4GDU6eki4b0tQ.B4hBROlgMlBduwloFtEfuV8.sPURLoC', 'jane.smith@example.com', 'USER', 2),
('admin', '$2a$10$Be/WwQvP4GDU6eki4b0tQ.B4hBROlgMlBduwloFtEfuV8.sPURLoC', 'admin@example.com', 'ADMIN', 3);
-- Insert sample rooms
INSERT INTO rooms (name, capacity, location, type, description) VALUES
('Conference Room A', 20, 'Building 1, Floor 1', 'CONFERENCE', 'Large conference room with projector'),
('Meeting Room B', 8, 'Building 1, Floor 2', 'MEETING', 'Small meeting room with whiteboard'),
('Training Room C', 30, 'Building 2, Floor 1', 'TRAINING', 'Training room with computers');

-- Insert sample equipment
INSERT INTO equipment (name, type, description) VALUES
('Projector 1', 'PROJECTOR', 'HD Projector with HDMI input'),
('Whiteboard 1', 'WHITEBOARD', 'Large magnetic whiteboard'),
('Conference Phone 1', 'PHONE', 'Polycom conference phone'),
('Laptop 1', 'COMPUTER', 'Dell laptop with presentation software');

-- Create indexes for better performance
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_room ON reservations(room_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_time ON reservations(start_time, end_time);
CREATE INDEX idx_equipment_available ON equipment(is_available);
CREATE INDEX idx_rooms_active ON rooms(is_active); 