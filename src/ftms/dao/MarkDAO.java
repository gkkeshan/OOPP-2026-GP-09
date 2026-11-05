package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Mark;

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

    /** mark_id is AUTO_INCREMENT, so we don't insert it; MySQL generates it and we copy it back. */
    @Override
    public boolean insert(Mark m) throws SQLException {
        m.setGrade(m.calculateGrade());
        String sql = "INSERT INTO mark (evaluation_id, student_id, marks, grade) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(m.getEvaluationId()));
            ps.setInt(2, Integer.parseInt(m.getStudentId()));
            ps.setDouble(3, m.getMarks());
            ps.setString(4, m.getGrade());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) m.setMarkId(String.valueOf(keys.getInt(1)));
                }
            }
            return ok;
        }
    }

    @Override
    public boolean update(Mark m) throws SQLException {
        m.setGrade(m.calculateGrade());
        String sql = "UPDATE mark SET marks=?, grade=? WHERE mark_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, m.getMarks());
            ps.setString(2, m.getGrade());
            ps.setInt(3, Integer.parseInt(m.getMarkId()));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM mark WHERE mark_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Mark findById(String id) throws SQLException {
        List<Mark> r = query("SELECT * FROM mark WHERE mark_id=?", Integer.valueOf(id));
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public List<Mark> findAll() throws SQLException {
        return query("SELECT * FROM mark", null);
    }

    public List<Mark> findByStudent(String studentId) throws SQLException {
        return query("SELECT * FROM mark WHERE student_id=?", Integer.valueOf(studentId));
    }

    public List<Mark> findByEvaluation(String evaluationId) throws SQLException {
        return query("SELECT * FROM mark WHERE evaluation_id=?", Integer.valueOf(evaluationId));
    }

    /** Marks of one student in one course (joins through evaluation). */
    public List<Mark> findByStudentAndCourse(String studentId, String courseCode) throws SQLException {
        String sql = "SELECT m.* FROM mark m JOIN evaluation e ON m.evaluation_id = e.evaluation_id "
                   + "WHERE m.student_id=? AND e.course_code=?";
        List<Mark> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(studentId));
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private List<Mark> query(String sql, Integer param) throws SQLException {
        List<Mark> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }
}
