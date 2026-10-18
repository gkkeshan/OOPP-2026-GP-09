package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.SemesterResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO implements IDatabaseOperations<SemesterResult> {

    private SemesterResult map(ResultSet rs) throws SQLException {
        return new SemesterResult(rs.getString("result_id"), rs.getString("student_id"),
                rs.getString("academic_year"), rs.getString("semester"),
                rs.getDouble("total_credits"), rs.getDouble("sgpa"), rs.getDouble("cgpa"));
    }

    @Override
    public boolean insert(SemesterResult r) throws SQLException {
        String sql = "INSERT INTO semester_result (student_id, academic_year, semester, total_credits, sgpa, cgpa) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(r.getStudentId()));
            ps.setString(2, r.getAcademicYear());
            ps.setString(3, r.getSemester());
            ps.setDouble(4, r.getTotalCredits());
            ps.setDouble(5, r.getSgpa());
            ps.setDouble(6, r.getCgpa());
            boolean inserted = ps.executeUpdate() > 0;
            if (inserted) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) r.setResultId(String.valueOf(keys.getInt(1)));
                }
            }
            return inserted;
        }
    }

    @Override
    public boolean update(SemesterResult r) throws SQLException {
        String sql = "UPDATE semester_result SET total_credits=?, sgpa=?, cgpa=? WHERE result_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, r.getTotalCredits());
            ps.setDouble(2, r.getSgpa());
            ps.setDouble(3, r.getCgpa());
            ps.setInt(4, Integer.parseInt(r.getResultId()));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM semester_result WHERE result_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public SemesterResult findById(String id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM semester_result WHERE result_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
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
            ps.setInt(1, Integer.parseInt(studentId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public SemesterResult findByStudentAndSemester(String studentId, String academicYear, String semester) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM semester_result WHERE student_id=? AND academic_year=? AND semester=?")) {
            ps.setInt(1, Integer.parseInt(studentId));
            ps.setString(2, academicYear);
            ps.setString(3, semester);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
}
