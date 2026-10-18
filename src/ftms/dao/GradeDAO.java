package ftms.dao;

import com.fot.model.Grade;
import com.fot.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Read-only access to the grading scale; the scale is fixed data, so no insert/update/delete. */
public class GradeDAO {

    private Grade map(ResultSet rs) throws SQLException {
        return new Grade(rs.getString("letter"), rs.getDouble("grade_point"),
                rs.getDouble("min_marks"), rs.getDouble("max_marks"), rs.getBoolean("is_pass"));
    }

    public List<Grade> findAll() throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM grade_scale ORDER BY min_marks DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Finds the grade band a final mark falls in (e.g. 77 -> A). Marks are rounded down to a whole number. */
    public Grade findByMarks(double marks) throws SQLException {
        int whole = (int) Math.floor(marks);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM grade_scale WHERE ? BETWEEN min_marks AND max_marks")) {
            ps.setInt(1, whole);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Grade findByLetter(String letter) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM grade_scale WHERE letter=?")) {
            ps.setString(1, letter);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
}
