package ftms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/ftams_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }


    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("DB Connection OK! -> " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("DB Connection FAILED: " + e.getMessage());
        }
    }
}