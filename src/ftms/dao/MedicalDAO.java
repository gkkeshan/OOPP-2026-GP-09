
package ftms.dao;

import ftms.model.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    // JOIN with undergraduate so every record carries the student's
    // registration number for display (medical.student_id is the internal
    // surrogate key, not the registration number).
    private static final String BASE_SELECT =
            "SELECT m.*, ug.registration_no AS registration_no "
                    + "FROM medical m "
                    + "LEFT JOIN undergraduate ug ON ug.student_id = m.student_id ";

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

    // Loads one medical record (used after a status change so the service can
    // link/unlink the affected attendance records).
    public Medical findById(Connection con, int medicalId) throws SQLException {
        String sql = BASE_SELECT + "WHERE m.medical_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, medicalId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Medical> getByStudent(Connection con, int studentId)
            throws SQLException {

        List<Medical> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE m.student_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<Medical> getAll(Connection con) throws SQLException {

        List<Medical> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(BASE_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // Resolves a registration number (e.g. TG2101) to the internal student_id
    // used by the medical / attendance tables. Returns -1 if not found.
    public int findStudentIdByRegistrationNo(Connection con, String regNo)
            throws SQLException {

        String sql = "SELECT student_id FROM undergraduate WHERE registration_no = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    // Shared row mapper: builds the Medical and attaches the registration no.
    private Medical mapRow(ResultSet rs) throws SQLException {
        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");

        Medical m = new Medical(
                rs.getInt("medical_id"),
                rs.getInt("student_id"),
                rs.getInt("to_id"),
                start == null ? null : start.toLocalDate(),
                end == null ? null : end.toLocalDate(),
                rs.getDate("submitted_date").toLocalDate(),
                rs.getString("approval_status"),
                rs.getString("document"));

        m.setRegistrationNo(rs.getString("registration_no"));
        return m;
    }
}
