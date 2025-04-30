-- Create the database
CREATE DATABASE IF NOT EXISTS waymate_db;
USE waymate_db;

-- Drop existing tables (optional during dev)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users;

-- Table: users
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE,
                       phone VARCHAR(20) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       role ENUM('admin', 'client') DEFAULT 'client',
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table: vehicles (cars, bikes, etc.)
CREATE TABLE vehicles (
                          license_plate VARCHAR(10) UNIQUE PRIMARY KEY,
                          type ENUM('voiture', 'vélo', 'trotinette', 'van') NOT NULL,
                          brand VARCHAR(50) NOT NULL,
                          model VARCHAR(50),
                          year INT,
                          status ENUM('disponible', 'loué', 'maintenance') DEFAULT 'disponible',
                          price_per_hour DECIMAL(10,2),
                          location_lat DECIMAL(9,6),    -- for GPS tracking
                          location_lng DECIMAL(9,6),    -- for GPS tracking
                          image_url VARCHAR(255)
);

-- Table: bookings
CREATE TABLE bookings (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT NOT NULL,
                          vehicle_license_plate VARCHAR(10) NOT NULL,
                          start_time DATETIME NOT NULL,
                          end_time DATETIME NOT NULL,
                          total_price DECIMAL(10,2),
                          status ENUM('en_attente', 'confirmée', 'annulée', 'terminée') DEFAULT 'en_attente',
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(id),
                          FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles(license_plate)
);

-- Table: payments
CREATE TABLE payments (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                          method VARCHAR(50),  -- e.g. 'credit_card', 'paypal', 'cash'
                          status ENUM('payé', 'en_attente', 'échoué') DEFAULT 'en_attente',
                          FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Table: reviews
CREATE TABLE reviews (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT NOT NULL,
                         vehicle_license_plate VARCHAR(10) NOT NULL,
                         rating INT CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles(license_plate)
);

-- Table: notifications
CREATE TABLE notifications (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               message TEXT NOT NULL,
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id)
);
