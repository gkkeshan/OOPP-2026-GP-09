
package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.AttendanceRecordDAO;
import ftms.dao.AttendanceSessionDAO;
import ftms.model.AttendanceRecord;
import ftms.model.AttendanceSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceService {

    private final AttendanceRecordDAO attendanceRecordDAO =
            new AttendanceRecordDAO();

    private final AttendanceSessionDAO attendanceSessionDAO =
            new AttendanceSessionDAO();

    // Get attendance records for a student and course.
    public List<AttendanceRecord> getAttendance(
            int studentId, String courseCode) throws SQLException {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            throw new SQLException("Database connection failed.");
        }

        return attendanceRecordDAO.getByStudentAndCourse(
                connection, studentId, courseCode);
    }

    // Save a session and create attendance records for enrolled students.
    public int addSession(String courseCode, int sessionNo,
                          LocalDate date, String sessionType, int toId)
            throws SQLException {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            throw new SQLException("Database connection failed.");
        }

        AttendanceSession session = new AttendanceSession(
                courseCode, sessionNo, date, sessionType);

        return attendanceSessionDAO.addSession(
                connection, session, toId);
    }

    // Get attendance records for a complete course.
    public List<AttendanceRecord> getAttendanceByCourse(
            String courseCode) throws SQLException {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            throw new SQLException("Database connection failed.");
        }

        return attendanceRecordDAO.getByCourse(
                connection, courseCode);
    }

    // Calculate Theory attendance percentage.
    public double getTheoryPercentage(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendance(studentId, courseCode);

        return calculatePercentage(records, "Theory");
    }

    // Calculate Practical attendance percentage.
    public double getPracticalPercentage(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendance(studentId, courseCode);

        return calculatePercentage(records, "Practical");
    }

    // Calculate Combined attendance percentage.
    public double getCombinedPercentage(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendance(studentId, courseCode);

        if (records.isEmpty()) {
            return 0.0;
        }

        int present = 0;

        for (AttendanceRecord record : records) {
            if ("Present".equals(record.getStatus())) {
                present++;
            }
        }

        return present * 100.0 / records.size();
    }

    // Method for integration with other modules.
    public double getPercentage(
            int studentId, String courseCode) throws SQLException {

        return getCombinedPercentage(studentId, courseCode);
    }

    // Calculate percentage for Theory or Practical.
    private double calculatePercentage(
            List<AttendanceRecord> records, String sessionType) {

        int total = 0;
        int present = 0;

        for (AttendanceRecord record : records) {
            if (sessionType.equals(record.getSessionType())) {
                total++;

                if ("Present".equals(record.getStatus())) {
                    present++;
                }
            }
        }

        if (total == 0) {
            return 0.0;
        }

        return present * 100.0 / total;
    }
}

