package ftms.dao;

import com.mysql.cj.xdevapi.DbDoc;
import ftms.config.DBConnection;
import ftms.exception.DuplicateUserException;
import ftms.model.*;

import java.sql.*;
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
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
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

        User user;
        switch (role.toUpperCase()) {
            case "ADMIN":
                user = new Admin(userId, userName, email, password, role, contactNumber);
                break;
            case "LECTURER":
                user = new Lecturer(userId, userName, email, password, role, contactNumber);
                break;
            case "TECHNICAL_OFFICER":
                user = new TechnicalOfficer(userId, userName, email, password, role, contactNumber);
                break;
            case "UNDERGRADUATE":
                user = new Undergraduate(userId, userName, email, password, role, contactNumber);
                break;
            default:
                return null;
        }
        user.setProfilePicture(rs.getString("profile_picture"));
        return user;

    }


    public User getUserById(int userId) {
        String query = "SELECT * FROM user WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? extractUserFromResultSet(rs) : null;
            }
        } catch (Exception e) {
            System.out.println("Error fetching user by id: " + e.getMessage());
            return null;
        }
    }

    public boolean updateContactNo(int userId, String contactNo) {
        return updateContactNumber(userId, contactNo);
    }
    public boolean updateEmail(int userId, String email) {
        String query = "UPDATE user SET email = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateProfilePicture(int userId, String path) {
        String query = "UPDATE user SET profile_picture = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, path);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Creates a user row AND its role row in ONE transaction.
// extra = designation (LECTURER) / department_id (TECHNICAL_OFFICER) / registration_no (UNDERGRADUATE)
    public int insertUser(String userName, String email, String hashedPassword,
                          String role, String contactNo, String extra)
            throws DuplicateUserException {

        String insertUserSql = "INSERT INTO user (user_name, email, password, role, contact_no) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);   // user row + role row must succeed together

            int newUserId;
            try {
                try (PreparedStatement ps = conn.prepareStatement(insertUserSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, userName);
                    ps.setString(2, email);
                    ps.setString(3, hashedPassword);
                    ps.setString(4, role);
                    ps.setString(5, contactNo);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        newUserId = keys.next() ? keys.getInt(1) : -1;
                    }
                }
                if (newUserId == -1) throw new SQLException("Could not read the new user id.");

                insertRoleRow(conn, role, newUserId, extra);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    throw new DuplicateUserException(
                            "That email or registration number is already used.");
                }
                System.out.println("Error creating user: " + e.getMessage());
                return -1;
            }
            return newUserId;
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return -1;
        }
    }

    // Inserts the matching row in the role table.
    private void insertRoleRow(Connection conn, String role, int userId, String extra) throws SQLException {
        switch (role.toUpperCase()) {
            case "LECTURER":
                try (PreparedStatement r = conn.prepareStatement(
                        "INSERT INTO lecturer (user_id, designation) VALUES (?, ?)")) {
                    r.setInt(1, userId);
                    r.setString(2, (extra == null || extra.isBlank()) ? "Lecturer" : extra.trim());
                    r.executeUpdate();
                }
                break;
            case "TECHNICAL_OFFICER":
                try (PreparedStatement r = conn.prepareStatement(
                        "INSERT INTO technical_officer (user_id, department_id) VALUES (?, ?)")) {
                    r.setInt(1, userId);
                    r.setInt(2, Integer.parseInt(extra == null ? "1" : extra.trim()));
                    r.executeUpdate();
                }
                break;
            case "UNDERGRADUATE":
                try (PreparedStatement r = conn.prepareStatement(
                        "INSERT INTO undergraduate (user_id, registration_no) VALUES (?, ?)")) {
                    r.setInt(1, userId);
                    r.setString(2, extra == null ? "" : extra.trim());
                    r.executeUpdate();
                }
                break;
            default:   // ADMIN — no extra row needed
                break;
        }
    }

    public boolean updateUser(int userId, String userName, String email, String contactNo) {
        String query = "UPDATE user SET user_name = ?, email = ?, contact_no = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, email);
            ps.setString(3, contactNo);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;   // role row removed by FK ON DELETE CASCADE
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean resetPassword(int userId, String hashedPassword) {
        String query = "UPDATE user SET password = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }
    public int getStudentIdByUserId(int userId) {
        String sql = "SELECT student_id FROM undergraduate WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Gets the human-readable registration number for the logged-in student.
    public String getRegistrationNoByUserId(int userId) {
        String sql = "SELECT registration_no FROM undergraduate WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
