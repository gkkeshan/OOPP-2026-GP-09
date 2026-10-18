package ftms.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;


public class Timetable extends BaseEntity {

    private int timetableId;
    private String courseCode;       // SCHEDULED (Course 1 : M TimeTable)
    private int venueId;             // Held_In   (TimeTable M : 1 Venue)
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionType sessionType;
    private LocalDate sessionDate;   // column add_date

    public Timetable(int timetableId, String courseCode, int venueId,
                     LocalTime startTime, LocalTime endTime, SessionType sessionType, LocalDate sessionDate) {
        this.timetableId = timetableId;
        setCourseCode(courseCode);
        setVenueId(venueId);
        setTimes(startTime, endTime);
        setSessionType(sessionType);
        setSessionDate(sessionDate);
    }

    public Timetable(String courseCode, int venueId,
                     LocalTime startTime, LocalTime endTime, SessionType sessionType, LocalDate sessionDate) {
        this(0, courseCode, venueId, startTime, endTime, sessionType, sessionDate);
    }

    public int getTimetableId() { return timetableId; }
    public void setTimetableId(int timetableId) { this.timetableId = timetableId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = requireText(courseCode, "Course code", 10).toUpperCase(); }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) {
        if (venueId <= 0) throw new IllegalArgumentException("A venue is required");
        this.venueId = venueId;
    }

    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }


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

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = requireNotNull(sessionDate, "Date"); }

    public double getDurationHours() {
        return Duration.between(startTime, endTime).toMinutes() / 60.0;
    }


    public boolean overlapsInTime(Timetable other) {
        return sessionDate.equals(other.sessionDate)
                && startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }


    public boolean clashesInVenue(Timetable other) {
        return venueId == other.venueId && overlapsInTime(other);
    }

    @Override public Object getId() { return timetableId; }

    @Override
    public String display() {
        return courseCode + " " + sessionType + " " + sessionDate + " " + startTime + "-" + endTime;
    }
}