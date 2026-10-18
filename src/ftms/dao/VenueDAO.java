package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Venue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    public int insert(Venue v) {
        String sql = "INSERT INTO venue (venue_name, building, capacity) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getVenueName());
            ps.setString(2, v.getBuilding());
            ps.setInt(3, v.getCapacity());
            ps.executeUpdate();
            return generatedKey(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save venue: " + e.getMessage(), e);
        }
    }

    public Venue findById(int venueId) {
        String sql = "SELECT * FROM venue WHERE venue_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load venue " + venueId + ": " + e.getMessage(), e);
        }
    }

    public List<Venue> findAll() {
        String sql = "SELECT * FROM venue ORDER BY venue_name";
        List<Venue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load venues: " + e.getMessage(), e);
        }
    }

    /** Venues that can hold at least the given number of students. */
    public List<Venue> findByMinCapacity(int minCapacity) {
        String sql = "SELECT * FROM venue WHERE capacity >= ? ORDER BY capacity";
        List<Venue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minCapacity);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search venues by capacity: " + e.getMessage(), e);
        }
    }

    public boolean update(Venue v) {
        String sql = "UPDATE venue SET venue_name = ?, building = ?, capacity = ? WHERE venue_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getVenueName());
            ps.setString(2, v.getBuilding());
            ps.setInt(3, v.getCapacity());
            ps.setInt(4, v.getVenueId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update venue: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int venueId) {
        String sql = "DELETE FROM venue WHERE venue_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, venueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete venue: " + e.getMessage(), e);
        }
    }

    private Venue mapRow(ResultSet rs) throws SQLException {
        return new Venue(
                rs.getInt("venue_id"),
                rs.getString("venue_name"),
                rs.getString("building"),
                rs.getInt("capacity"));
    }

    private int generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}