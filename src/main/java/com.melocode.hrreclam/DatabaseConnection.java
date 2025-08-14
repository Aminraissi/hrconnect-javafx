package com.melocode.hrreclam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hrconnect"; // Ensure database exists
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Change if necessary

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database!");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Reclamation table
            String reclamationTable = """
                CREATE TABLE IF NOT EXISTS reclamation (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_name VARCHAR(100) NOT NULL,
                    type ENUM('Workplace Harassment', 'Salary Issue', 'Working Conditions', 'Other') NOT NULL,
                    description TEXT NOT NULL,
                    date_of_submission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status ENUM('Pending', 'In Progress', 'Resolved', 'Rejected') DEFAULT 'Pending',
                    priority ENUM('Low', 'Medium', 'High') NOT NULL
                ) ENGINE=InnoDB;
            """;

            // Ticket reclamation table
            String ticketReclamationTable = """
                CREATE TABLE IF NOT EXISTS ticket_reclamation (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    reclamation_id INT NOT NULL,
                    hr_staff_name VARCHAR(100) NOT NULL,
                    response_message TEXT,
                    date_of_response TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    action_taken TEXT,
                    resolution_status ENUM('Open', 'Resolved', 'Escalated', 'Closed') DEFAULT 'Open',
                    FOREIGN KEY (reclamation_id) REFERENCES reclamation(id) ON DELETE CASCADE
                ) ENGINE=InnoDB;
            """;

            stmt.execute(reclamationTable);
            stmt.execute(ticketReclamationTable);
            System.out.println("✅ Tables created successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}
