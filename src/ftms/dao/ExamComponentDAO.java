package com.fot.dao;

import com.fot.model.ExamComponent;
import com.fot.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamComponentDAO implements IDatabaseOperations<ExamComponent> {

    private ExamComponent map(ResultSet rs) throws SQLException {
        return new ExamComponent(
                rs.getString("evaluation_id"), rs.getString("course_code"),
                rs.getString("evaluation_name"), rs.getInt("max_marks"),
                rs.getDouble("weight"), rs.getBoolean("is_ca"));
    }

    @Override
    public boolean insert(ExamComponent e) throws SQLException {
        String sql = "INSERT INTO evaluation (evaluation_id, course_code, evaluation_name, max_marks, weight, is_ca) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getEvaluationId());
            ps.setString(2, e.getCourseCode());
            ps.setString(3, e.getEvaluationName());
            ps.setInt(4, e.getMaxMarks());
            ps.setDouble(5, e.getWeight());
            ps.setBoolean(6, e.isCA());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ExamComponent e) throws SQLException {
        String sql = "UPDATE evaluation SET course_code=?, evaluation_name=?, max_marks=?, weight=?, is_ca=? WHERE evaluation_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getCourseCode());
            ps.setString(2, e.getEvaluationName());
            ps.setInt(3, e.getMaxMarks());
            ps.setDouble(4, e.getWeight());
            ps.setBoolean(5, e.isCA());
            ps.setString(6, e.getEvaluationId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM evaluation WHERE evaluation_id=?")) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ExamComponent findById(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM evaluation WHERE evaluation_id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<ExamComponent> findAll() throws SQLException {
        List<ExamComponent> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM evaluation")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** All components of a course; used by the marks entry screen. */
    public List<ExamComponent> findByCourse(String courseCode) throws SQLException {
        List<ExamComponent> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM evaluation WHERE course_code=?")) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }
}
