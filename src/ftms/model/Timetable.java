package ftms.model;

import java.time.Duration;
import java.time.LocalTime;


public class Timetable extends BaseEntity {

    private static final String[] DAYS =
            {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    private int timetableId;
    private String courseCode;       // SCHEDULED (Course 1 : M TimeTable)
    private int batchId;             // timetable is shown per batch
    private int venueId;             // Held_In   (TimeTable M : 1 Venue)
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionType sessionType;
    private String dayOfWeek;        // column day_of_week

    public Timetable(int timetableId, String courseCode, int batchId, int venueId,
                     LocalTime startTime, LocalTime endTime, SessionType sessionType, String dayOfWeek) {
        this.timetableId = timetableId;
        setCourseCode(courseCode);
        setBatchId(batchId);
        setVenueId(venueId);
        setTimes(startTime, endTime);
        setSessionType(sessionType);
        setDayOfWeek(dayOfWeek);
    }

    public Timetable(String courseCode, int batchId, int venueId,
                     LocalTime startTime, LocalTime endTime, SessionType sessionType, String dayOfWeek) {
        this(0, courseCode, batchId, venueId, startTime, endTime, sessionType, dayOfWeek);
    }

    public int getTimetableId() { return timetableId; }
    public void setTimetableId(int timetableId) { this.timetableId = timetableId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = requireText(courseCode, "Course code", 10).toUpperCase(); }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) {
        if (batchId <= 0) throw new IllegalArgumentException("A batch is required");
        this.batchId = batchId;
    }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) {
        if (venueId <= 0) throw new IllegalArgumentException("A venue is required");
        this.venueId = venueId;
    }

    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    /** Start and end are set together so the end can always be checked against the start. */
    public void setTimes(LocalTime startTime, LocalTime endTime) {
        requireNotNull(startTime, "Start time");
        requireNotNull(endTime, "End time");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = requireNotNull(sessionType, "Session type"); }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) {
        String day = requireText(dayOfWeek, "Day of week", 10).toUpperCase();
        for (String d : DAYS) {
            if (d.equals(day)) {
                this.dayOfWeek = day;
                return;
            }
        }
        throw new IllegalArgumentException("Day must be one of MONDAY..SUNDAY");
    }

    public double getDurationHours() {
        return Duration.between(startTime, endTime).toMinutes() / 60.0;
    }

    /** True if the two classes are on the same weekday and their times overlap (touching end/start is NOT an overlap). */
    public boolean overlapsInTime(Timetable other) {
        return dayOfWeek.equals(other.dayOfWeek)
                && startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }

    /** True if both classes use the same venue at overlapping times on the same day - the clash the Timetable service must stop. */
    public boolean clashesInVenue(Timetable other) {
        return venueId == other.venueId && overlapsInTime(other);
    }

    @Override public Object getId() { return timetableId; }

    @Override
    public String display() {
        return courseCode + " " + sessionType + " " + dayOfWeek + " " + startTime + "-" + endTime;
    }
}