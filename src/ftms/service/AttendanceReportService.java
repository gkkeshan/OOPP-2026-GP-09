
package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.AttendanceRecordDAO;
import ftms.model.AttendanceRecord;
import ftms.model.AttendanceSummary;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AttendanceReportService {

    private AttendanceRecordDAO attendanceRecordDAO =
            new AttendanceRecordDAO();

    public AttendanceSummary getAttendanceSummary(
            int studentId, String courseCode) throws SQLException {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            throw new SQLException("Database connection failed.");
        }

        List<AttendanceRecord> records =
                attendanceRecordDAO.getByStudentAndCourse(
                        connection, studentId, courseCode);

        int theoryPresent = 0;
        int theoryTotal = 0;
        int practicalPresent = 0;
        int practicalTotal = 0;

        for (AttendanceRecord record : records) {

            if ("Theory".equals(record.getSessionType())) {
                theoryTotal++;

                if ("Present".equals(record.getStatus())) {
                    theoryPresent++;
                }

            } else if ("Practical".equals(record.getSessionType())) {
                practicalTotal++;

                if ("Present".equals(record.getStatus())) {
                    practicalPresent++;
                }
            }
        }

        return new AttendanceSummary(
                studentId,
                courseCode,
                theoryPresent,
                theoryTotal,
                practicalPresent,
                practicalTotal
        );
    }
}