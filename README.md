# Room Booking System

A Java Swing application for managing room reservations in an organization.

## Features

- User authentication (admin and regular users)
- Room management (create, edit, delete rooms)
- Reservation management (create, cancel reservations)
- Calendar view for room availability
- Email notifications for reservation confirmations and cancellations
- Admin dashboard for system management
- User dashboard for making and managing reservations

## Technologies

- Java 17
- Swing for GUI
- MySQL for database
- Maven for dependency management
- JBCrypt for password hashing
- JavaMail for email notifications
- SLF4J and Logback for logging
- JFreeChart for reporting

## Setup Instructions

### Prerequisites

- Java 17 or higher
- MySQL Server
- Maven

### Database Setup

1. Install MySQL Server
2. Run the setup script: `setup_database.bat` (Windows) or `setup_database.sh` (Linux/Mac)
3. The default admin credentials are:
   - Username: admin
   - Password: admin123

### Configuration

1. Update database connection settings in `src/main/resources/database.properties`
2. Update email settings in `src/main/java/com/roombooking/util/EmailService.java`

### Building and Running

1. Build the project: `mvn clean package`
2. Run the application: `java -jar target/room-booking-app-1.0-SNAPSHOT.jar`

## Usage

1. Log in with the default admin credentials or register a new user account
2. Admin users can manage rooms, users, and approve/reject reservations
3. Regular users can browse available rooms and make reservation requests

## License

This project is licensed under the MIT License - see the LICENSE file for details. 