
package ftms.service;

import ftms.config.DBConnection;
import ftms.dao.MedicalDAO;
import ftms.model.Medical;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MedicalService {

    private MedicalDAO medicalDAO = new MedicalDAO();

    public boolean addMedical(Medical medical) throws SQLException {
        Connection connection = DBConnection.getConnection();
        return medicalDAO.addMedical(connection, medical);
    }

    public boolean updateStatus(int medicalId, String status)
            throws SQLException {

        Connection connection = DBConnection.getConnection();

        return medicalDAO.updateStatus(connection, medicalId, status);
    }

    public List<Medical> getByStudent(int studentId) throws SQLException {
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
}
