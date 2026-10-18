package ftms.dao;

import ftms.model.Medical;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    // Saves a new medical record. The database generates medical_id.
    public boolean addMedical(Connection connection, Medical medical) throws SQLException {
        String sql = "INSERT INTO medical " +
                "(student_id, to_id, medical_date, submitted_date, approval_status, document) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, medical.getStudentId());
            ps.setInt(2, medical.getToId());
            ps.setDate(3, Date.valueOf(medical.getMedicalDate()));
            ps.setDate(4, Date.valueOf(medical.getSubmittedDate()));
            ps.setString(5, medical.getApprovalStatus());
            ps.setString(6, medical.getDocument());
            return ps.executeUpdate() > 0;
        }
    }

    // TO can approve or reject a medical record.
    public boolean updateStatus(Connection connection, int medicalId, String status)
            throws SQLException {
        String sql = "UPDATE medical SET approval_status = ? WHERE medical_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, medicalId);
            return ps.executeUpdate() > 0;
        }
    }

    // Gets medical records for a student.
    public List<Medical> getByStudent(Connection connection, int studentId) throws SQLException {
        List<Medical> medicals = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE student_id = ? ORDER BY submitted_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicals.add(new Medical(
                            rs.getInt("medical_id"),
                            rs.getInt("student_id"),
                            rs.getInt("to_id"),
                            rs.getDate("medical_date").toLocalDate(),
                            rs.getDate("submitted_date").toLocalDate(),
                            rs.getString("approval_status"),
                            rs.getString("document")));
                }
            }
        }
        return medicals;
    }

    // Gets all medical records for the TO/admin/lecturer views.
    public List<Medical> getAll(Connection connection) throws SQLException {
        List<Medical> medicals = new ArrayList<>();
        String sql = "SELECT * FROM medical ORDER BY submitted_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicals.add(new Medical(
                        rs.getInt("medical_id"),
                        rs.getInt("student_id"),
                        rs.getInt("to_id"),
                        rs.getDate("medical_date").toLocalDate(),
                        rs.getDate("submitted_date").toLocalDate(),
                        rs.getString("approval_status"),
                        rs.getString("document")));
            }
        }
        return medicals;
    }
}