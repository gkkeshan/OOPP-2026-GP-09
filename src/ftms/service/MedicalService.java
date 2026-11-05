package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.AttendanceRecordDAO;
import ftms.dao.MedicalDAO;
import ftms.dao.UserDAO;
import ftms.model.Medical;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MedicalService {

    private MedicalDAO medicalDAO = new MedicalDAO();
    private AttendanceRecordDAO attendanceRecordDAO =
            new AttendanceRecordDAO();
    private UserDAO userDAO = new UserDAO();


    public boolean addMedical(Medical medical) throws SQLException {

        // Check whether the medical is submitted within 14 days
        // from the absence start date.
        long daysBetween = ChronoUnit.DAYS.between(
                medical.getStartDate(),
                medical.getSubmittedDate()
        );

        if (daysBetween < 0) {
            throw new SQLException(
                    "Submitted date cannot be before the absence date."
            );
        }

        if (daysBetween > 14) {
            throw new SQLException(
                    "Medical must be submitted within 14 days of the absence date."
            );
        }

        Connection connection = DBConnection.getConnection();

        return medicalDAO.addMedical(connection, medical);
    }


    public boolean updateStatus(int medicalId, String status)
            throws SQLException {

        Connection connection = DBConnection.getConnection();

        boolean ok = medicalDAO.updateStatus(
                connection, medicalId, status);

        // If the medical is approved, link the student's absent
        // attendance records within the medical date range.
        // If rejected, remove the medical link.
        if (ok) {

            Medical medical = medicalDAO.findById(
                    connection, medicalId);

            if (medical != null) {

                attendanceRecordDAO.applyMedical(
                        medical.getStudentId(),
                        medicalId,
                        medical.getStartDate(),
                        medical.getEndDate(),
                        "Approved".equals(status)
                );
            }
        }

        return ok;
    }


    public List<Medical> getByStudent(int studentId)
            throws SQLException {

        Connection connection = DBConnection.getConnection();

        return medicalDAO.getByStudent(
                connection, studentId);
    }


    public List<Medical> getByStudent(String registrationNo)
            throws SQLException {

        int studentId =
                userDAO.getStudentIdByRegistrationNo(
                        registrationNo);

        if (studentId == -1) {
            throw new SQLException(
                    "Student not found with registration number: "
                            + registrationNo);
        }

        Connection connection = DBConnection.getConnection();

        return medicalDAO.getByStudent(
                connection, studentId);
    }


    public List<Medical> getAll()
            throws SQLException {

        Connection connection = DBConnection.getConnection();

        return medicalDAO.getAll(connection);
    }


    // Translates the logged-in user's user_id into student_id.
    public int getStudentIdByUserId(int userId) {

        return new UserDAO()
                .getStudentIdByUserId(userId);
    }


    // Resolves a registration number like TG2101
    // to the internal student_id.
    public int findStudentIdByRegistrationNo(String regNo)
            throws SQLException {

        Connection connection =
                DBConnection.getConnection();

        return medicalDAO.findStudentIdByRegistrationNo(
                connection, regNo);
    }
}
