
package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.AttendanceRecordDAO;
import ftms.dao.MedicalDAO;
import ftms.dao.UserDAO;
import ftms.model.Medical;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MedicalService {

    private MedicalDAO medicalDAO = new MedicalDAO();
    private AttendanceRecordDAO attendanceRecordDAO = new AttendanceRecordDAO();
    private UserDAO userDAO = new UserDAO();



    public boolean addMedical(Medical medical) throws SQLException {
        Connection connection = DBConnection.getConnection();
        return medicalDAO.addMedical(connection, medical);
    }

    public boolean updateStatus(int medicalId, String status)
            throws SQLException {

        Connection connection = DBConnection.getConnection();
        boolean ok = medicalDAO.updateStatus(connection, medicalId, status);
        // Approved medical -> the student's ABSENT sessions inside the medical's
        // date range are linked to it (attendance_record.medical_id), so reports
        // and eligibility excuse them. Pending/Rejected removes the link again.
        if (ok) {
            Medical medical = medicalDAO.findById(connection, medicalId);
            if (medical != null) {
                attendanceRecordDAO.applyMedical(
                        medical.getStudentId(), medicalId,
                        medical.getStartDate(), medical.getEndDate(),
                        "Approved".equals(status));
            }
        }

        return ok;
    }

    public List<Medical> getByStudent(int studentId) throws SQLException {
        Connection connection = DBConnection.getConnection();
        return medicalDAO.getByStudent(connection, studentId);
    }

    public List<Medical> getByStudent(String registrationNo) throws SQLException {
        int studentId = userDAO.getStudentIdByRegistrationNo(registrationNo);
        if (studentId == -1) {
            throw new SQLException("Student not found with registration number: " + registrationNo);
        }
        Connection connection = DBConnection.getConnection();
        return medicalDAO.getByStudent(connection, studentId);
    }

    public List<Medical> getAll() throws SQLException {
        Connection connection = DBConnection.getConnection();
        return medicalDAO.getAll(connection);
    }

    // Translates the logged-in user's user_id into student_id,
    // because the medical table references undergraduate.student_id.
    public int getStudentIdByUserId(int userId){
        return new ftms.dao.UserDAO().getStudentIdByUserId(userId);
    }


    // Resolves a registration number like "TG2101" to the internal student_id.
    // Returns -1 when no student has that registration number.
    public int findStudentIdByRegistrationNo(String regNo) throws SQLException {
        Connection connection = DBConnection.getConnection();
        return medicalDAO.findStudentIdByRegistrationNo(connection, regNo);
    }
}
