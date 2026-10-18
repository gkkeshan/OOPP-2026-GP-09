
package ftms.model;

import java.time.LocalDate;

public class AttendanceRecord {

    private int attendanceId;
    private String courseCode;
    private int studentId;
    private int sessionNo;
    private LocalDate date;
    private String sessionType;
    private String status;
    private int toId;
    private Integer medicalId;

    public AttendanceRecord() {
    }

    public AttendanceRecord(int attendanceId, String courseCode,
                            int studentId, int sessionNo,
                            LocalDate date, String sessionType,
                            String status, int toId,
                            Integer medicalId) {
        this.attendanceId = attendanceId;
        this.courseCode = courseCode;
        this.studentId = studentId;
        this.sessionNo = sessionNo;
        this.date = date;
        setSessionType(sessionType);
        setStatus(status);
        this.toId = toId;
        this.medicalId = medicalId;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null
                && !status.equals("Present")
                && !status.equals("Absent")) {
            throw new IllegalArgumentException(
                    "Status must be Present or Absent");
        }

        this.status = status;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public Integer getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(Integer medicalId) {
        this.medicalId = medicalId;
    }
}