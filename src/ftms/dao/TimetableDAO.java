package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.SessionType;
import ftms.model.Timetable;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class TimetableDAO {

    public int insert(Timetable t) {
        String sql = "INSERT INTO timetable (course_code, venue_id, start_time, end_time, session_type, add_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getCourseCode());
            ps.setInt(2, t.getVenueId());
            ps.setTime(3, Time.valueOf(t.getStartTime()));
            ps.setTime(4, Time.valueOf(t.getEndTime()));
            ps.setString(5, t.getSessionType().getDbValue());
            ps.setDate(6, Date.valueOf(t.getSessionDate()));
            ps.executeUpdate();
            return generatedKey(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save timetable entry: " + e.getMessage(), e);
        }
    }

    public Timetable findById(int timetableId) {
        String sql = "SELECT * FROM timetable WHERE timetable_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, timetableId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load timetable entry " + timetableId + ": " + e.getMessage(), e);
        }
    }

    public List<Timetable> findAll() {
        String sql = "SELECT * FROM timetable ORDER BY add_date, start_time";
        List<Timetable> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load timetable: " + e.getMessage(), e);
        }
    }

    public List<Timetable> findByCourse(String courseCode) {
        String sql = "SELECT * FROM timetable WHERE course_code = ? ORDER BY add_date, start_time";
        List<Timetable> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load timetable of course: " + e.getMessage(), e);
        }
    }

    public List<Timetable> findByDate(LocalDate date) {
        String sql = "SELECT * FROM timetable WHERE add_date = ? ORDER BY start_time";
        List<Timetable> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load timetable of date: " + e.getMessage(), e);
        }
    }

    /**
     * True if the same venue is already booked by ANOTHER entry on the same date
     * with overlapping times. The TimetableService calls this before saving.
     */
    public boolean hasVenueClash(Timetable t) {
        String sql = "SELECT COUNT(*) FROM timetable "
                + "WHERE venue_id = ? AND add_date = ? AND timetable_id <> ? "
                + "AND start_time < ? AND end_time > ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getVenueId());
            ps.setDate(2, Date.valueOf(t.getSessionDate()));
            ps.setInt(3, t.getTimetableId());
            ps.setTime(4, Time.valueOf(t.getEndTime()));
            ps.setTime(5, Time.valueOf(t.getStartTime()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check venue clash: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int timetableId) {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete timetable entry: " + e.getMessage(), e);
        }
    }

    private Timetable mapRow(ResultSet rs) throws SQLException {
        LocalTime start = rs.getTime("start_time").toLocalTime();
        LocalTime end = rs.getTime("end_time").toLocalTime();
        return new Timetable(
                rs.getInt("timetable_id"),
                rs.getString("course_code"),
                rs.getInt("venue_id"),
                start,
                end,
                SessionType.fromDb(rs.getString("session_type")),
                rs.getDate("add_date").toLocalDate());
    }

    private int generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}