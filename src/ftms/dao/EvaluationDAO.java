package dao;

import model.Evaluation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO implements IDatabaseOperations {

    // DB column "percentage" <-> model field "weight"
    private Evaluation map(ResultSet rs) throws SQLException {
        return new Evaluation(rs.getString("evaluation_id"), rs.getString("course_code"),
                rs.getString("evaluation_name"), rs.getInt("max_marks"),
                rs.getDouble("percentage"), rs.getBoolean("is_ca"));
    }

    /** evaluation_id is AUTO_INCREMENT, so we don't insert it; MySQL generates it and we copy it back. */
    @Override
    public boolean insert(Object obj) throws SQLException {
        Evaluation e = (Evaluation) obj;
        String sql = "INSERT INTO evaluation (course_code, evaluation_name, max_marks, percentage, is_ca) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getCourseCode());
            ps.setString(2, e.getEvaluationName());
            ps.setInt(3, e.getMaxMarks());
            ps.setDouble(4, e.getWeight());
            ps.setBoolean(5, e.isCA());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) e.setEvaluationId(String.valueOf(keys.getInt(1)));
                }
            }
            return ok;
        }
    }

    @Override
    public boolean update(Object obj) throws SQLException {
        Evaluation e = (Evaluation) obj;
        String sql = "UPDATE evaluation SET course_code=?, evaluation_name=?, max_marks=?, percentage=?, is_ca=? WHERE evaluation_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getCourseCode());
            ps.setString(2, e.getEvaluationName());
            ps.setInt(3, e.getMaxMarks());
            ps.setDouble(4, e.getWeight());
            ps.setBoolean(5, e.isCA());
            ps.setInt(6, Integer.parseInt(e.getEvaluationId()));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM evaluation WHERE evaluation_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Evaluation findById(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM evaluation WHERE evaluation_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Evaluation> findAll() throws SQLException {
        List<Evaluation> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM evaluation")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Evaluation> findByCourse(String courseCode) throws SQLException {
        List<Evaluation> list = new ArrayList<>();
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
