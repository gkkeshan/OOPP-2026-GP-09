package com.fot.dao;

import com.fot.model.Mark;
import com.fot.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO implements IDatabaseOperations<Mark> {

    private Mark map(ResultSet rs) throws SQLException {
        Mark m = new Mark(rs.getString("mark_id"), rs.getString("evaluation_id"),
                rs.getString("student_id"), rs.getDouble("marks"));
        m.setGrade(rs.getString("grade"));
        return m;
    }

    @Override
    public boolean insert(Mark m) throws SQLException {
        String sql = "INSERT INTO mark (mark_id, evaluation_id, student_id, marks, grade) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getMarkId());
            ps.setString(2, m.getEvaluationId());
            ps.setString(3, m.getStudentId());
            ps.setDouble(4, m.getMarks());
            ps.setString(5, m.getGrade());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Mark m) throws SQLException {
        String sql = "UPDATE mark SET marks=?, grade=? WHERE mark_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, m.getMarks());
            ps.setString(2, m.getGrade());
            ps.setString(3, m.getMarkId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM mark WHERE mark_id=?")) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Mark findById(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM mark WHERE mark_id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Mark> findAll() throws SQLException {
        return query("SELECT * FROM mark", null);
    }

    public List<Mark> findByStudent(String studentId) throws SQLException {
        return query("SELECT * FROM mark WHERE student_id=?", studentId);
    }

    public List<Mark> findByEvaluation(String evaluationId) throws SQLException {
        return query("SELECT * FROM mark WHERE evaluation_id=?", evaluationId);
    }

    /** All marks of one student in one course (joins through evaluation). */
    public List<Mark> findByStudentAndCourse(String studentId, String courseCode) throws SQLException {
        String sql = "SELECT m.* FROM mark m JOIN evaluation e ON m.evaluation_id = e.evaluation_id "
                   + "WHERE m.student_id=? AND e.course_code=?";
        List<Mark> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private List<Mark> query(String sql, String param) throws SQLException {
        List<Mark> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }
}
