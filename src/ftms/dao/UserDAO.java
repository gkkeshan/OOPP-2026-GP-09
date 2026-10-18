package ftms.dao;

import com.mysql.cj.xdevapi.DbDoc;
import ftms.config.DBConnection;
import ftms.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";

        try(Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            // Execute the query and process the ResultSet
            // (You would typically return a User object here)
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching user by email: " + e.getMessage());
            e.printStackTrace();

        }
        return null; // Placeholder return
    }

    public List<User> getAllUsers() {
        // Implement logic to fetch all users from the database
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery();
        ){
            while (rs.next()){
                User user = extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Placeholder return
    }

    public boolean updateContactNumber(int userId, String newContactNumber){
        String query = "UPDATE user SET contact_no = ? WHERE user_id = ? ";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1,newContactNumber);
            pstmt.setInt(2,userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating contact number: " + e.getMessage());
            throw new RuntimeException(e);
        }


    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String userName = rs.getString("user_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String role = rs.getString("role");
        String contactNumber = rs.getString("contact_no");

        if(role == null) return null;

        switch (role.toUpperCase()) {
            case  "ADMIN":
                return new Admin(userId, userName, email, password, role, contactNumber);

            case "LECTURE":
                return  new Lecturer(userId, userName, email, password, role, contactNumber);

            case "TECHNICAL_OFFICER":
                return  new TechnicalOfficer(userId, userName, email, password, role, contactNumber);

            case "UNDERGRADUATE":
                return new Undergraduate(userId, userName, email, password, role, contactNumber);

        }


        return null;
    }



}
