@echo off
echo Setting up the Room Booking System Database...

REM Check if MySQL is installed and in PATH
mysql --version > nul 2>&1
if %errorlevel% neq 0 (
    echo MySQL is not found. Please install MySQL and add it to your PATH
    pause
    exit /b 1
)

REM Set database credentials
set DB_USER=root
set DB_PASS=Yahya101@
set DB_NAME=room_booking

REM Create database and tables
echo Creating database and tables...
mysql --user=%DB_USER% --password="%DB_PASS%" < setup_database.sql

if %errorlevel% equ 0 (
    echo Database setup completed successfully!
    echo.
    echo Database: %DB_NAME%
    echo Default admin credentials:
    echo Username: admin
    echo Password: admin123
    echo.
    echo Please update the database.properties file if you changed any credentials.
) else (
    echo Error setting up the database. Please check your MySQL installation and credentials.
)

pause 