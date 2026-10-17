package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Batch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BatchDAO {

    public int insert(Batch b) {
        String sql = "INSERT INTO batch (batch_name, academic_year, department_id) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getBatchName());
            ps.setString(2, b.getAcademicYear());
            ps.setInt(3, b.getDepartmentId());
            ps.executeUpdate();
            return generatedKey(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save batch: " + e.getMessage(), e);
        }
    }

    public Batch findById(int batchId) {
        String sql = "SELECT * FROM batch WHERE batch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load batch " + batchId + ": " + e.getMessage(), e);
        }
    }

    public List<Batch> findAll() {
        String sql = "SELECT * FROM batch ORDER BY batch_name";
        List<Batch> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load batches: " + e.getMessage(), e);
        }
    }

    public List<Batch> findByDepartment(int departmentId) {
        String sql = "SELECT * FROM batch WHERE department_id = ? ORDER BY batch_name";
        List<Batch> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load batches of department: " + e.getMessage(), e);
        }
    }

    public boolean nameExistsInDepartment(String batchName, int departmentId) {
        String sql = "SELECT COUNT(*) FROM batch WHERE batch_name = ? AND department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, batchName.trim());
            ps.setInt(2, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check batch name: " + e.getMessage(), e);
        }
    }

    public boolean update(Batch b) {
        String sql = "UPDATE batch SET batch_name = ?, academic_year = ?, department_id = ? WHERE batch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getBatchName());
            ps.setString(2, b.getAcademicYear());
            ps.setInt(3, b.getDepartmentId());
            ps.setInt(4, b.getBatchId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update batch: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int batchId) {
        String sql = "DELETE FROM batch WHERE batch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, batchId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete batch: " + e.getMessage(), e);
        }
    }

    private Batch mapRow(ResultSet rs) throws SQLException {
        return new Batch(
                rs.getInt("batch_id"),
                rs.getString("batch_name"),
                rs.getString("academic_year"),
                rs.getInt("department_id"));
    }

    private int generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}