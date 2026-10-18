package ftms.model;

import ftms.exception.EligibilityException;

public class EligibilityStatus {
    private String studentId;
    private String courseCode;
    private double attendancePercentage;
    private double caPercentage;
    private boolean attendanceEligible;
    private boolean caEligible;

    public EligibilityStatus() {}

    public EligibilityStatus(String studentId, String courseCode, double attendancePercentage,
                             double caPercentage) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        setAttendancePercentage(attendancePercentage);
        setCaPercentage(caPercentage);
        this.attendanceEligible = attendancePercentage >= 80;
        this.caEligible = caPercentage >= 40;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) {
        if (!Double.isFinite(attendancePercentage)
                || attendancePercentage < 0 || attendancePercentage > 100) {
            throw new EligibilityException("Attendance percentage must be between 0 and 100");
        }
        this.attendancePercentage = attendancePercentage;
        this.attendanceEligible = attendancePercentage >= 80;
    }
    public double getCaPercentage() { return caPercentage; }
    public void setCaPercentage(double caPercentage) {
        if (!Double.isFinite(caPercentage) || caPercentage < 0 || caPercentage > 100) {
            throw new EligibilityException("CA percentage must be between 0 and 100");
        }
        this.caPercentage = caPercentage;
        this.caEligible = caPercentage >= 40;
    }
    public boolean isAttendanceEligible() { return attendanceEligible; }
    public boolean isCaEligible() { return caEligible; }
    public boolean isEligible() { return attendanceEligible && caEligible; }
    public String getFailureReason() {
        if (isEligible()) return "";
        if (!attendanceEligible && !caEligible) return "Attendance is below 80% and CA is below 40%";
        return attendanceEligible ? "CA is below 40%" : "Attendance is below 80%";
    }
}
