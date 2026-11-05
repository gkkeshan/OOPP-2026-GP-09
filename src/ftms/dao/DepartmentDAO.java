package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DepartmentDAO {

    public int insert(Department d) {
        String sql = "INSERT INTO department (department_name) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getDepartmentName());
            ps.executeUpdate();
            return generatedKey(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save department: " + e.getMessage(), e);
        }
    }

    public Department findById(int departmentId) {
        String sql = "SELECT * FROM department WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load department " + departmentId + ": " + e.getMessage(), e);
        }
    }

    public Department findByName(String departmentName) {
        String sql = "SELECT * FROM department WHERE department_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, departmentName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not search department: " + e.getMessage(), e);
        }
    }

    public List<Department> findAll() {
        String sql = "SELECT * FROM department ORDER BY department_name";
        List<Department> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load departments: " + e.getMessage(), e);
        }
    }

    public boolean nameExists(String departmentName) {
        String sql = "SELECT COUNT(*) FROM department WHERE department_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, departmentName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check department name: " + e.getMessage(), e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM department";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not count departments: " + e.getMessage(), e);
        }
    }

    public boolean update(Department d) {
        String sql = "UPDATE department SET department_name = ? WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentName());
            ps.setInt(2, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update department: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int departmentId) {
        String sql = "DELETE FROM department WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete department: " + e.getMessage(), e);
        }
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        return new Department(
                rs.getInt("department_id"),
                rs.getString("department_name"));
    }

    private int generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}