package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Enrollment;
import ftms.model.EnrollmentStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnrollmentDAO {

    public boolean insert(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment (student_id, course_code, academic_year, semester, enrollment_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setString(2, enrollment.getCourseCode());
            ps.setString(3, enrollment.getAcademicYear());
            ps.setString(4, enrollment.getSemester());
            ps.setDate(5, Date.valueOf(enrollment.getEnrollmentDate()));
            ps.setString(6, enrollment.getStatus().getDbValue());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save enrollment: " + e.getMessage(), e);
        }
    }

    public Enrollment findById(int enrollmentId) {
        String sql = "SELECT * FROM enrollment WHERE enrollment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load enrollment " + enrollmentId + ": " + e.getMessage(), e);
        }
    }

    public List<Enrollment> findByStudent(int studentId) {
        String sql = "SELECT * FROM enrollment WHERE student_id = ? ORDER BY enrollment_date DESC";
        List<Enrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load enrollments for student: " + e.getMessage(), e);
        }
    }

    public List<Enrollment> findByStudentAndCourse(int studentId, String courseCode) {
        String sql = "SELECT * FROM enrollment WHERE student_id = ? AND course_code = ?";
        List<Enrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load enrollments: " + e.getMessage(), e);
        }
    }

    public boolean update(Enrollment enrollment) {
        String sql = "UPDATE enrollment SET student_id = ?, course_code = ?, academic_year = ?, " +
                "semester = ?, enrollment_date = ?, status = ? WHERE enrollment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setString(2, enrollment.getCourseCode());
            ps.setString(3, enrollment.getAcademicYear());
            ps.setString(4, enrollment.getSemester());
            ps.setDate(5, Date.valueOf(enrollment.getEnrollmentDate()));
            ps.setString(6, enrollment.getStatus().getDbValue());
            ps.setInt(7, enrollment.getEnrollmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update enrollment: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int enrollmentId) {
        String sql = "DELETE FROM enrollment WHERE enrollment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete enrollment: " + e.getMessage(), e);
        }
    }

    public boolean existsByStudentAndCourse(int studentId, String courseCode) {
        String sql = "SELECT COUNT(*) FROM enrollment WHERE student_id = ? AND course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check enrollment Exists: " + e.getMessage(), e);
        }
    }

    public List<Map.Entry<Integer, String>> findEligibilityTargets(
            Integer studentId, String courseCode) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT student_id, course_code FROM enrollment WHERE status <> 'Dropped'");
        if (studentId != null) {
            sql.append(" AND student_id = ?");
        }
        if (courseCode != null && !courseCode.isBlank()) {
            sql.append(" AND course_code LIKE ?");
        }
        sql.append(" ORDER BY course_code, student_id");

        List<Map.Entry<Integer, String>> targets = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            if (studentId != null) {
                ps.setInt(parameterIndex++, studentId);
            }
            if (courseCode != null && !courseCode.isBlank()) {
                ps.setString(parameterIndex, "%" + courseCode.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    targets.add(new AbstractMap.SimpleImmutableEntry<>(
                            rs.getInt("student_id"), rs.getString("course_code")));
                }
            }
        }
        return targets;
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        return new Enrollment(
                rs.getInt("enrollment_id"),
                rs.getInt("student_id"),
                rs.getString("course_code"),
                rs.getString("academic_year"),
                rs.getString("semester"),
                rs.getDate("enrollment_date").toLocalDate(),
                EnrollmentStatus.fromDb(rs.getString("status"))
        );
    }
}
