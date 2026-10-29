package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CourseDAO {

    public boolean insert(Course c) {
        String sql = "INSERT INTO course (course_code, course_name, credit_hours, "
                + "has_theory, has_practical, lecturer_id, department_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setInt(3, c.getCreditHours());
            ps.setBoolean(4, c.isHasTheory());
            ps.setBoolean(5, c.isHasPractical());
            ps.setInt(6, c.getLecturerId());
            ps.setInt(7, c.getDepartmentId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save course: " + e.getMessage(), e);
        }
    }

    public Course findById(String courseCode) {
        String sql = "SELECT * FROM course WHERE course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load course " + courseCode + ": " + e.getMessage(), e);
        }
    }

    public List<Course> findAll() {
        String sql = "SELECT * FROM course ORDER BY course_code";
        List<Course> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load courses: " + e.getMessage(), e);
        }
    }

    public List<Course> findByDepartment(int departmentId) {
        String sql = "SELECT * FROM course WHERE department_id = ? ORDER BY course_code";
        List<Course> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load courses of department: " + e.getMessage(), e);
        }
    }

    public List<Course> findByLecturer(int lecturerId) {
        String sql = "SELECT * FROM course WHERE lecturer_id = ? ORDER BY course_code";
        List<Course> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load courses of lecturer: " + e.getMessage(), e);
        }
    }
    public List<Course> findByStudent(int userId) {
        String sql = "SELECT c.* FROM enrollment e " +
                "JOIN undergraduate ug ON ug.student_id = e.student_id " +
                "JOIN course c ON c.course_code = e.course_code " +
                "WHERE ug.user_id = ? ORDER BY c.course_code";
        List<Course> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load student courses: " + e.getMessage(), e);
        }
    }

    public boolean existsById(String courseCode) {
        String sql = "SELECT COUNT(*) FROM course WHERE course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check course code: " + e.getMessage(), e);
        }
    }

    public boolean update(Course c) {
        String sql = "UPDATE course SET course_name = ?, credit_hours = ?, has_theory = ?, "
                + "has_practical = ?, lecturer_id = ?, department_id = ? WHERE course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setInt(2, c.getCreditHours());
            ps.setBoolean(3, c.isHasTheory());
            ps.setBoolean(4, c.isHasPractical());
            ps.setInt(5, c.getLecturerId());
            ps.setInt(6, c.getDepartmentId());
            ps.setString(7, c.getCourseCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update course: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(String courseCode) {
        String sql = "DELETE FROM course WHERE course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete course: " + e.getMessage(), e);
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getInt("credit_hours"),
                rs.getBoolean("has_theory"),
                rs.getBoolean("has_practical"),
                rs.getInt("lecturer_id"),
                rs.getInt("department_id"));
    }
}