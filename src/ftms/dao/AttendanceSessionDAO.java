package ftms.dao;

import ftms.model.AttendanceSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSessionDAO {

    // Creates a session by adding an Absent record for each enrolled student.
    public int addSession(Connection connection, AttendanceSession session, int toId)
            throws SQLException {
        String sql = "INSERT INTO attendance_record " +
                "(course_code, student_id, session_no, date, session_type, status, to_id) " +
                "SELECT ?, student_id, ?, ?, ?, 'Absent', ? " +
                "FROM enrollment WHERE course_code = ? AND status <> 'Dropped'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, session.getCourseCode());
            ps.setInt(2, session.getSessionNo());
            ps.setDate(3, Date.valueOf(session.getDate()));
            ps.setString(4, session.getSessionType());
            ps.setInt(5, toId);
            ps.setString(6, session.getCourseCode());
            return ps.executeUpdate();
        }
    }

    // Gets the sessions already saved for a course.
    public List<AttendanceSession> getSessions(Connection connection, String courseCode)
            throws SQLException {
        List<AttendanceSession> sessions = new ArrayList<>();
        String sql = "SELECT course_code, session_no, date, session_type " +
                "FROM attendance_record WHERE course_code = ? " +
                "GROUP BY course_code, session_no, date, session_type " +
                "ORDER BY session_type, session_no";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(new AttendanceSession(
                            rs.getString("course_code"),
                            rs.getInt("session_no"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("session_type")));
                }
            }
        }
        return sessions;
    }
}