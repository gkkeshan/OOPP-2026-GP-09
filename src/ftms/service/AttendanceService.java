
package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.AttendanceRecordDAO;
import ftms.dao.AttendanceSessionDAO;
import ftms.dao.UserDAO;
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

    private final UserDAO userDAO = new UserDAO();

    // Get attendance records for a student and course by registration number.
    public List<AttendanceRecord> getAttendance(
            String registrationNo, String courseCode) throws SQLException {
        Integer studentId = null;
        if (registrationNo != null && !registrationNo.isBlank()) {
            int resolvedStudentId = userDAO.getStudentIdByRegistrationNo(registrationNo.trim());
            if (resolvedStudentId == -1) {
                throw new SQLException("Student not found with registration number: "
                        + registrationNo);
            }
            studentId = resolvedStudentId;
        }

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }
            return attendanceRecordDAO.getFiltered(connection, studentId, courseCode);
        }
    }

    // Get attendance records for a student and course by student ID (legacy method).
    public List<AttendanceRecord> getAttendanceById(
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

    // Calculate Theory attendance percentage by registration number.
    public double getTheoryPercentage(
            String registrationNo, String courseCode) throws SQLException {

        int studentId = userDAO.getStudentIdByRegistrationNo(registrationNo);
        if (studentId == -1) {
            throw new SQLException("Student not found with registration number: " + registrationNo);
        }

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

        return calculatePercentage(records, "Theory");
    }

    // Calculate Theory attendance percentage by student ID (legacy).
    public double getTheoryPercentageById(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

        return calculatePercentage(records, "Theory");
    }

    // Calculate Practical attendance percentage by registration number.
    public double getPracticalPercentage(
            String registrationNo, String courseCode) throws SQLException {

        int studentId = userDAO.getStudentIdByRegistrationNo(registrationNo);
        if (studentId == -1) {
            throw new SQLException("Student not found with registration number: " + registrationNo);
        }

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

        return calculatePercentage(records, "Practical");
    }

    // Calculate Practical attendance percentage by student ID (legacy).
    public double getPracticalPercentageById(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

        return calculatePercentage(records, "Practical");
    }

    // Calculate Combined attendance percentage by registration number.
    public double getCombinedPercentage(
            String registrationNo, String courseCode) throws SQLException {

        int studentId = userDAO.getStudentIdByRegistrationNo(registrationNo);
        if (studentId == -1) {
            throw new SQLException("Student not found with registration number: " + registrationNo);
        }

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

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

    // Calculate Combined attendance percentage by student ID (legacy).
    public double getCombinedPercentageById(
            int studentId, String courseCode) throws SQLException {

        List<AttendanceRecord> records =
                getAttendanceById(studentId, courseCode);

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

    // Method for integration with other modules by registration number.
    public double getPercentage(
            String registrationNo, String courseCode) throws SQLException {

        return getCombinedPercentage(registrationNo, courseCode);
    }

    // Method for integration with other modules by student ID (legacy).
    public double getPercentageById(
            int studentId, String courseCode) throws SQLException {

        return getCombinedPercentageById(studentId, courseCode);
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
