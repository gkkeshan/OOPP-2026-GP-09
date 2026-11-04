package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.AttendanceRecord;
import ftms.model.AttendanceSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRecordDAO {

    // Changes a student's attendance status for a session.
    public boolean updateStatus(Connection connection, int attendanceId, String status)
            throws SQLException {
        String sql = "UPDATE attendance_record SET status = ? WHERE attendance_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, attendanceId);
            return ps.executeUpdate() > 0;
        }
    }

    // Gets attendance records for one student in one course.
    public List<AttendanceRecord> getByStudentAndCourse(
            Connection connection, int studentId, String courseCode) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance_record " +
                "WHERE student_id = ? AND course_code = ? ORDER BY date, session_no";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date sqlDate = rs.getDate("date");
                    records.add(new AttendanceRecord(
                            rs.getInt("attendance_id"),
                            rs.getString("course_code"),
                            rs.getInt("student_id"),
                            rs.getInt("session_no"),
                            sqlDate.toLocalDate(),
                            rs.getString("session_type"),
                            rs.getString("status"),
                            rs.getInt("to_id"),
                            (Integer) rs.getObject("medical_id")));
                }
            }
        }
        return records;
    }

    // Gets all attendance records for a course.
    public List<AttendanceRecord> getByCourse(Connection connection, String courseCode)
            throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance_record WHERE course_code = ? ORDER BY date, student_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new AttendanceRecord(
                            rs.getInt("attendance_id"),
                            rs.getString("course_code"),
                            rs.getInt("student_id"),
                            rs.getInt("session_no"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("session_type"),
                            rs.getString("status"),
                            rs.getInt("to_id"),
                            (Integer) rs.getObject("medical_id")));
                }
            }
        }
        return records;
    }
    /** One AttendanceSummary per student for a whole course (batch summary). */
    public List<AttendanceSummary> getBatchSummaries(String courseCode) throws SQLException {
        String sql = "SELECT student_id, "
                + "SUM(CASE WHEN session_type='Theory' AND status='Present' THEN 1 ELSE 0 END) AS th_present, "
                + "SUM(CASE WHEN session_type='Theory' AND medical_id IS NULL THEN 1 ELSE 0 END) AS th_total, "
                + "SUM(CASE WHEN session_type='Practical' AND status='Present' THEN 1 ELSE 0 END) AS pr_present, "
                + "SUM(CASE WHEN session_type='Practical' AND medical_id IS NULL THEN 1 ELSE 0 END) AS pr_total "
                + "FROM attendance_record WHERE course_code = ? "
                + "GROUP BY student_id ORDER BY student_id";
        List<AttendanceSummary> summaries = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new AttendanceSummary(
                            rs.getInt("student_id"), courseCode.trim(),
                            rs.getInt("th_present"), rs.getInt("th_total"),
                            rs.getInt("pr_present"), rs.getInt("pr_total")));
                }
            }
        }
        return summaries;
    }
    // Links a student's ABSENT sessions inside the medical's date range to the
// medical record (attendance_record.medical_id), so that reports excuse
// them. When the medical is NOT approved, the link is removed again.
    public int applyMedical(int studentId, int medicalId,
                            LocalDate start, LocalDate end, boolean approved)
            throws SQLException {
        if (approved && (start == null || end == null)) {
            return 0;   // cannot excuse without a date range
        }
        String sql = approved
                ? "UPDATE attendance_record SET medical_id = ? "
                + "WHERE student_id = ? AND status = 'Absent' AND date BETWEEN ? AND ?"
                : "UPDATE attendance_record SET medical_id = NULL WHERE medical_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (approved) {
                ps.setInt(1, medicalId);
                ps.setInt(2, studentId);
                ps.setDate(3, Date.valueOf(start));
                ps.setDate(4, Date.valueOf(end));
            } else {
                ps.setInt(1, medicalId);
            }
            return ps.executeUpdate();
        }
    }
}
