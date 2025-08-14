package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDb {
    private String url = "jdbc:mysql://localhost:3306/hrconnect";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static MyDb instance;

    public static MyDb getInstance() {
        if (instance == null) {
            instance = new MyDb();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    private MyDb() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}