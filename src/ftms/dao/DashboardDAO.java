package ftms.dao;

import ftms.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Read-only aggregation queries for the role dashboards. No write methods. */
public class DashboardDAO {

    public int countUsers() throws SQLException {
        return count("SELECT COUNT(*) FROM user");
    }

    public int countCourses() throws SQLException {
        return count("SELECT COUNT(*) FROM course");
    }

    public int countStudents() throws SQLException {
        return count("SELECT COUNT(*) FROM undergraduate");
    }

    public int countMedicals() throws SQLException {
        return count("SELECT COUNT(*) FROM medical");
    }

    public int countPendingMedicals() throws SQLException {
        return countWhere("SELECT COUNT(*) FROM medical WHERE approval_status = ?",
                pstmt -> pstmt.setString(1, "Pending"));
    }

    public int countNotices() throws SQLException {
        return count("SELECT COUNT(*) FROM notice");
    }

    public int countActiveEnrollments(int studentId) throws SQLException {
        return countWhere("SELECT COUNT(*) FROM enrollment WHERE student_id = ? AND status = 'Active'",
                pstmt -> pstmt.setInt(1, studentId));
    }

    public int countPendingMedicalsFor(int studentId) throws SQLException {
        return countWhere("SELECT COUNT(*) FROM medical WHERE student_id = ? AND approval_status = 'Pending'",
                pstmt -> pstmt.setInt(1, studentId));
    }

    /** Latest CGPA for a student; null if the student has no results yet. */
    public Double latestCgpa(int studentId) throws SQLException {
        String sql = "SELECT cgpa FROM semester_result WHERE student_id = ? ORDER BY result_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : null;
            }
        }
    }

    /** Attendance percentage; null when the student has no attendance records. */
    public Integer attendancePercent(int studentId) throws SQLException {
        String sql = "SELECT ROUND(100.0 * SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) / COUNT(*)) "
                + "FROM attendance_record WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1) != null ? rs.getInt(1) : null;
                }
                return null;
            }
        }
    }

    private int count(String sql) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private int countWhere(String sql, SqlBinder binder) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            binder.bind(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement pstmt) throws SQLException;
    }
}