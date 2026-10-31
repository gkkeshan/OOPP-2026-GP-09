
package ftms.dao;

import ftms.model.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public boolean addMedical(Connection con, Medical m) throws SQLException {

        // Get TO ID using User ID
        String findTO = "SELECT to_id FROM technical_officer WHERE user_id = ?";
        PreparedStatement findPS = con.prepareStatement(findTO);
        findPS.setInt(1, m.getToId());

        ResultSet rs = findPS.executeQuery();

        if (!rs.next()) {
            throw new SQLException("Technical Officer not found!");
        }

        int toId = rs.getInt("to_id");

        // Insert medical record
        String sql = "INSERT INTO medical (student_id, to_id, start_date, end_date, submitted_date, approval_status, document) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, m.getStudentId());
        ps.setInt(2, toId);
        ps.setDate(3, Date.valueOf(m.getStartDate()));
        ps.setDate(4, Date.valueOf(m.getEndDate()));
        ps.setDate(5, Date.valueOf(m.getSubmittedDate()));
        ps.setString(6, m.getApprovalStatus());
        ps.setString(7, m.getDocument());

        return ps.executeUpdate() > 0;
    }

    public boolean updateStatus(Connection con, int id, String status)
            throws SQLException {

        String sql = "UPDATE medical SET approval_status = ? WHERE medical_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, status);
        ps.setInt(2, id);

        return ps.executeUpdate() > 0;
    }

    public List<Medical> getByStudent(Connection con, int studentId)
            throws SQLException {

        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE student_id = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, studentId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Medical(
                    rs.getInt("medical_id"),
                    rs.getInt("student_id"),
                    rs.getInt("to_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDate("submitted_date").toLocalDate(),
                    rs.getString("approval_status"),
                    rs.getString("document")
            ));
        }

        return list;
    }

    public List<Medical> getAll(Connection con) throws SQLException {

        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical";

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Medical(
                    rs.getInt("medical_id"),
                    rs.getInt("student_id"),
                    rs.getInt("to_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDate("submitted_date").toLocalDate(),
                    rs.getString("approval_status"),
                    rs.getString("document")
            ));
        }

        return list;
    }
}
