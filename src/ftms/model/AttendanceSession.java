package ftms.model;

import java.time.LocalDate;

public class AttendanceSession {

    private String courseCode;
    private int sessionNo;
    private LocalDate date;
    private String sessionType;

    public AttendanceSession() {
    }

    public AttendanceSession(String courseCode, int sessionNo,
                             LocalDate date, String sessionType) {
        this.courseCode = courseCode;
        this.sessionNo = sessionNo;
        this.date = date;
        setSessionType(sessionType);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getSessionNo() {
        return sessionNo;
    }

    public void setSessionNo(int sessionNo) {
        this.sessionNo = sessionNo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        if (sessionType != null
                && !sessionType.equals("Theory")
                && !sessionType.equals("Practical")) {
            throw new IllegalArgumentException(
                    "Session type must be Theory or Practical");
        }

        this.sessionType = sessionType;
    }
}