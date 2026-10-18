package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public int insert(CourseMaterial m) {
        String sql = "INSERT INTO course_material (course_code, title, file_path, uploaded_date) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getCourseCode());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getFilePath());
            ps.setDate(4, Date.valueOf(m.getUploadedDate()));
            ps.executeUpdate();
            return generatedKey(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save course material: " + e.getMessage(), e);
        }
    }

    public CourseMaterial findById(int materialId) {
        String sql = "SELECT * FROM course_material WHERE material_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load material " + materialId + ": " + e.getMessage(), e);
        }
    }

    public List<CourseMaterial> findByCourse(String courseCode) {
        String sql = "SELECT * FROM course_material WHERE course_code = ? ORDER BY uploaded_date DESC, material_id DESC";
        List<CourseMaterial> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load materials of course: " + e.getMessage(), e);
        }
    }

    public List<CourseMaterial> findAll() {
        String sql = "SELECT * FROM course_material ORDER BY uploaded_date DESC, material_id DESC";
        List<CourseMaterial> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load course materials: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int materialId) {
        String sql = "DELETE FROM course_material WHERE material_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete course material: " + e.getMessage(), e);
        }
    }

    private CourseMaterial mapRow(ResultSet rs) throws SQLException {
        return new CourseMaterial(
                rs.getInt("material_id"),
                rs.getString("course_code"),
                rs.getString("title"),
                rs.getString("file_path"),
                rs.getDate("uploaded_date").toLocalDate());
    }

    private int generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}