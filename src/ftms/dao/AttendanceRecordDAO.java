package ftms.dao;

import ftms.model.AttendanceRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
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
}
