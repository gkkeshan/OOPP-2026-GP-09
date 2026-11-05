package ftms.dao;

import ftms.config.DBConnection;
import ftms.exception.DuplicateUserException;
import ftms.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        enrichUndergraduates(users);
        return users;
    }

    // Fills registration_no / batch / academic_year into the Undergraduate objects
// so the Users table can show each student's batch (one extra query, no N+1).
    private void enrichUndergraduates(List<User> users) {
        Map<Integer, Undergraduate> byUserId = new HashMap<>();
        for (User u : users) {
            if (u instanceof Undergraduate) byUserId.put(u.getUserID(), (Undergraduate) u);
        }
        if (byUserId.isEmpty()) return;
        String sql = "SELECT user_id, registration_no, batch, academic_year, batch_id FROM undergraduate";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Undergraduate ug = byUserId.get(rs.getInt("user_id"));
                if (ug != null) {
                    ug.setRegistration_no(rs.getString("registration_no"));
                    ug.setBatch(rs.getString("batch"));
                    ug.setAcademic_year(rs.getString("academic_year"));
                    int bid = rs.getInt("batch_id");
                    ug.setBatch_id(rs.wasNull() ? null : String.valueOf(bid));
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not load student batch info: " + e.getMessage());
        }
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
        user.setNic(rs.getString("nic"));
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
                          String role, String contactNo, String nic, String extra, Batch batch)
            throws DuplicateUserException {

        String insertUserSql = "INSERT INTO user (user_name, email, password, role, contact_no, nic, profile_picture) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                    ps.setString(6, nic);
                    ps.setString(7, "profile_pics/default.jpg");
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        newUserId = keys.next() ? keys.getInt(1) : -1;
                    }
                }
                if (newUserId == -1) throw new SQLException("Could not read the new user id.");

                insertRoleRow(conn, role, newUserId, extra, batch);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    throw new DuplicateUserException(
                            "That email, NIC, or registration number is already used.");
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
    private void insertRoleRow(Connection conn, String role, int userId, String extra,Batch batch) throws SQLException {
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
                        "INSERT INTO undergraduate (user_id, registration_no, batch, academic_year, batch_id) VALUES (?, ?, ?, ?, ?)")) {
                    r.setInt(1, userId);
                    r.setString(2, extra == null ? "" : extra.trim());
                    if (batch != null) {          // copy name/year so lists can show them without a JOIN
                        r.setString(3, batch.getBatchName());
                        r.setString(4, batch.getAcademicYear());
                        r.setInt(5, batch.getBatchId());
                    } else {
                        r.setNull(3, Types.VARCHAR);
                        r.setNull(4, Types.VARCHAR);
                        r.setNull(5, Types.INTEGER);
                    }
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
    public int getAdminIdByUserId(int userId) {
        String sql = "SELECT admin_id FROM admin WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }

        } catch (SQLException e) {
            System.err.println("Error resolving admin id: " + e.getMessage());
            return -1;
        }
    }

    public int getStudentIdByRegistrationNo(String registrationNo) {
        String sql = "SELECT student_id FROM undergraduate WHERE registration_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registrationNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("Error resolving student id by registration number: " + e.getMessage());
            return -1;
        }
    }

    public int getDepartmentIdByStudentId(int studentId) {
        String sql = "SELECT b.department_id FROM undergraduate ug " +
                "JOIN batch b ON ug.batch_id = b.batch_id WHERE ug.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("Error resolving department id by student id: " + e.getMessage());
            return -1;
        }
    }

    public int getBatchIdByStudentId(int studentId) {
        String sql = "SELECT batch_id FROM undergraduate WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("Error resolving batch id by student id: " + e.getMessage());
            return -1;
        }
    }

    // Updates all users without a profile picture to use the default one
    public int setDefaultProfilePictureForAllUsers() {
        String sql = "UPDATE user SET profile_picture = 'profile_pics/default.jpg' WHERE profile_picture IS NULL OR profile_picture = ''";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error setting default profile pictures: " + e.getMessage());
            return 0;
        }
    }
}
