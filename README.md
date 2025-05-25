# Room Booking System

A Java Swing application for managing room reservations in an organization.

## Overview

This application provides a comprehensive solution for room booking management with separate interfaces for administrators and regular users.

## Features

- **User Management**
  - User authentication (admin and regular users)
  - User registration and profile management

- **Room Management**
  - Create, edit, and delete rooms
  - View room details and availability

- **Reservation System**
  - Create and cancel reservations
  - Calendar view for room availability
  - Email notifications for confirmations and cancellations

- **Admin Features**
  - Admin dashboard for system management
  - Reservation approval workflow
  - User management

## Technologies

- **Backend**: Java 17
- **Frontend**: Java Swing
- **Database**: MySQL
- **Dependencies**: 
  - Maven for dependency management
  - JBCrypt for password hashing
  - JavaMail for email notifications
  - SLF4J and Logback for logging
  - JFreeChart for reporting

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL Server
- Maven

### Installation

1. Clone the repository
2. Install MySQL Server
3. Run the setup script: 
   ```
   setup_database.bat    # Windows
   setup_database.sh     # Linux/Mac
   ```
4. Update configuration files:
   - Database: `src/main/resources/database.properties`
   - Email: `src/main/java/com/roombooking/util/EmailService.java`

### Building and Running

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/room-booking-app-1.0-SNAPSHOT.jar
```

## Usage

### Initial Login

Default admin credentials:
- Username: admin
- Password: yahya123

### Basic Workflow

1. **Administrators**:
   - Manage rooms (add, edit, delete)
   - Manage users
   - Review and approve reservations

2. **Regular Users**:
   - Browse available rooms
   - Make reservation requests
   - View and cancel personal reservations

## License

This project is licensed under the MIT License - see the LICENSE file for details. 