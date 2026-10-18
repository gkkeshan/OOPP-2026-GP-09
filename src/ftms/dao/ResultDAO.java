package com.fot.dao;

import com.fot.model.SemesterResult;
import com.fot.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO implements IDatabaseOperations<SemesterResult> {

    private SemesterResult map(ResultSet rs) throws SQLException {
        return new SemesterResult(rs.getString("result_id"), rs.getString("student_id"),
                rs.getString("academic_year"), rs.getInt("semester"),
                rs.getDouble("total_credits"), rs.getDouble("sgpa"), rs.getDouble("cgpa"));
    }

    @Override
    public boolean insert(SemesterResult r) throws SQLException {
        String sql = "INSERT INTO semester_result (result_id, student_id, academic_year, semester, total_credits, sgpa, cgpa) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getResultId());
            ps.setString(2, r.getStudentId());
            ps.setString(3, r.getAcademicYear());
            ps.setInt(4, r.getSemester());
            ps.setDouble(5, r.getTotalCredits());
            ps.setDouble(6, r.getSgpa());
            ps.setDouble(7, r.getCgpa());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(SemesterResult r) throws SQLException {
        String sql = "UPDATE semester_result SET total_credits=?, sgpa=?, cgpa=? WHERE result_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, r.getTotalCredits());
            ps.setDouble(2, r.getSgpa());
            ps.setDouble(3, r.getCgpa());
            ps.setString(4, r.getResultId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM semester_result WHERE result_id=?")) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public SemesterResult findById(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM semester_result WHERE result_id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<SemesterResult> findAll() throws SQLException {
        List<SemesterResult> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM semester_result")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<SemesterResult> findByStudent(String studentId) throws SQLException {
        List<SemesterResult> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM semester_result WHERE student_id=? ORDER BY academic_year, semester")) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public SemesterResult findByStudentAndSemester(String studentId, String academicYear, int semester) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM semester_result WHERE student_id=? AND academic_year=? AND semester=?")) {
            ps.setString(1, studentId);
            ps.setString(2, academicYear);
            ps.setInt(3, semester);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
}
