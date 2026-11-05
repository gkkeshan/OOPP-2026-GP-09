package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.SessionType;
import ftms.model.Timetable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for table `timetable` (ER entity TimeTable).
 * Member 2 - Academic Structure module.
 *
 * Table columns (ftams_db.sql):
 *   timetable_id (PK, AUTO_INCREMENT), course_code (FK SCHEDULED),
 *   batch_id (FK - one weekly timetable per batch), venue_id (FK HELD_IN),
 *   start_time, end_time (TIME, CHECK end > start),
 *   session_type (CHECK 'Theory'/'Practical'),
 *   day_of_week (VARCHAR 'MONDAY'..'SUNDAY' - the class repeats every week).
 *
 * The venue clash test {@link #hasVenueClash(Timetable)} is the SQL side of
 * Timetable.overlapsInTime(...) - the TimetableService uses it before saving.
 */
public class TimetableDAO {

    // Day order for sorting - without FIELD() MySQL sorts the names alphabetically (FRIDAY first).
    private static final String DAY_ORDER =
            "FIELD(day_of_week,'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')";

    // ------------------------------------------------------------------ CREATE

    /** Saves one scheduled class and returns the generated timetable_id. */
    public int insert(Timetable timetable) {
        String sql = "INSERT INTO timetable "
                + "(course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, timetable);
            ps.executeUpdate();
            return generatedKey(ps);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "TimetableDAO: could not save session for course '" + timetable.getCourseCode() + "'", e);
        }
    }

    // -------------------------------------------------------------------- READ

    /** Returns one scheduled class, or null when the id does not exist. */
    public Timetable findById(int timetableId) {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable WHERE timetable_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, timetableId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("TimetableDAO: could not load timetable id " + timetableId, e);
        }
    }

    /** All scheduled classes, weekday order (Mon..Sun) then start time. Never null. */
    public List<Timetable> findAll() {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable ORDER BY " + DAY_ORDER + ", start_time";
        return runListQuery(sql, statement -> {});
    }

    /** All classes of one course (ER: SCHEDULED, Course 1 : M TimeTable). */
    public List<Timetable> findByCourse(String courseCode) {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable WHERE course_code = ? ORDER BY " + DAY_ORDER + ", start_time";
        return runListQuery(sql, statement -> statement.setString(1, courseCode.trim().toUpperCase()));
    }

    /** All classes of one batch - that batch's own weekly timetable. */
    public List<Timetable> findByBatch(int batchId) {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable WHERE batch_id = ? ORDER BY " + DAY_ORDER + ", start_time";
        return runListQuery(sql, statement -> statement.setInt(1, batchId));
    }

    /** All classes on one weekday, e.g. every Monday class (weekly timetable view). */
    public List<Timetable> findByDay(String dayOfWeek) {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable WHERE day_of_week = ? ORDER BY start_time";
        return runListQuery(sql, statement -> statement.setString(1, dayOfWeek.trim().toUpperCase()));
    }

    /** All classes of one venue on one weekday (clash checking / venue duty roster). */
    public List<Timetable> findByVenueAndDay(int venueId, String dayOfWeek) {
        String sql = "SELECT timetable_id, course_code, batch_id, venue_id, start_time, end_time, session_type, day_of_week "
                + "FROM timetable WHERE venue_id = ? AND day_of_week = ? ORDER BY start_time";
        return runListQuery(sql, statement -> {
            statement.setInt(1, venueId);
            statement.setString(2, dayOfWeek.trim().toUpperCase());
        });
    }

    /**
     * True when the SAME venue already has a class that OVERLAPS this one on
     * the same weekday. Touching sessions (one ends 12:00, next starts 12:00)
     * are NOT a clash. The venue is physically occupied no matter which batch
     * is inside, so batch_id is NOT part of this test. The row being edited
     * skips itself (timetable_id 0 never matches, so a new session is checked
     * against everything).
     */
    public boolean hasVenueClash(Timetable timetable) {
        String sql = "SELECT 1 FROM timetable "
                + "WHERE venue_id = ? AND day_of_week = ? "
                + "AND start_time < ? AND end_time > ? AND timetable_id <> ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, timetable.getVenueId());
            ps.setString(2, timetable.getDayOfWeek());
            ps.setTime(3, Time.valueOf(timetable.getEndTime()));    // existing starts before new ends
            ps.setTime(4, Time.valueOf(timetable.getStartTime()));  // existing ends after new starts
            ps.setInt(5, timetable.getTimetableId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("TimetableDAO: could not check venue clash", e);
        }
    }

    /** Total number of scheduled classes (for dashboards). */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM timetable";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new RuntimeException("TimetableDAO: could not count timetable rows", e);
        }
    }

    // ------------------------------------------------------------------ UPDATE

    /** Updates course, batch, venue, times, session type and weekday. False = id not found. */
    public boolean update(Timetable timetable) {
        String sql = "UPDATE timetable SET course_code = ?, batch_id = ?, venue_id = ?, start_time = ?, "
                + "end_time = ?, session_type = ?, day_of_week = ? WHERE timetable_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            fillStatement(ps, timetable);
            ps.setInt(8, timetable.getTimetableId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "TimetableDAO: could not update timetable id " + timetable.getTimetableId(), e);
        }
    }

    // ------------------------------------------------------------------ DELETE

    /** Deletes one scheduled class. False = id not found. */
    public boolean deleteById(int timetableId) {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("TimetableDAO: could not delete timetable id " + timetableId, e);
        }
    }

    /**
     * Deletes every class of one course - used when a course is removed.
     * Returns how many rows were deleted. (The FK would cascade this anyway,
     * but the explicit count lets the UI confirm the number to the admin.)
     */
    public int deleteByCourse(String courseCode) {
        String sql = "DELETE FROM timetable WHERE course_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseCode.trim().toUpperCase());
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "TimetableDAO: could not delete sessions of course '" + courseCode + "'", e);
        }
    }

    // ------------------------------------------------------------------ INTERN

    /** Sets the 7 write parameters in the same order as the INSERT column list. */
    private void fillStatement(PreparedStatement ps, Timetable timetable) throws SQLException {
        ps.setString(1, timetable.getCourseCode());
        ps.setInt(2, timetable.getBatchId());
        ps.setInt(3, timetable.getVenueId());
        ps.setTime(4, Time.valueOf(timetable.getStartTime()));
        ps.setTime(5, Time.valueOf(timetable.getEndTime()));
        ps.setString(6, timetable.getSessionType().getDbValue());  // 'Theory' / 'Practical'
        ps.setString(7, timetable.getDayOfWeek());                 // 'MONDAY' .. 'SUNDAY'
    }

    /** Turns the current ResultSet row into a Timetable object. */
    private Timetable mapRow(ResultSet rs) throws SQLException {
        return new Timetable(
                rs.getInt("timetable_id"),
                rs.getString("course_code"),
                rs.getInt("batch_id"),
                rs.getInt("venue_id"),
                rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime(),
                SessionType.fromDb(rs.getString("session_type")),
                rs.getString("day_of_week"));
    }

    /** Shared loop for the list queries so each one stays a two-liner. */
    private List<Timetable> runListQuery(String sql, PreparedStatementSetter setter) {
        List<Timetable> sessions = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRow(rs));
                }
            }
            return sessions;

        } catch (SQLException e) {
            throw new RuntimeException("TimetableDAO: query failed - " + e.getMessage(), e);
        }
    }

    /** One-line parameter filler used by {@link #runListQuery(String, PreparedStatementSetter)}. */
    @FunctionalInterface
    private interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    /** Reads the AUTO_INCREMENT id produced by the last INSERT. */
    private int generatedKey(Statement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            return keys.next() ? keys.getInt(1) : 0;
        }
    }
}