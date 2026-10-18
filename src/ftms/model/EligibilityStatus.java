package com.fot.model;

/**
 * Computed result (not stored in DB) of the two eligibility rules for one student + course:
 * attendance >= 80% and CA >= 40%. Built by EligibilityService.
 */
public class EligibilityStatus {
    public static final double MIN_ATTENDANCE = 80.0;
    public static final double MIN_CA = 40.0;

    private String studentId;
    private String courseCode;
    private double caPercentage;
    private double attendancePercentage;

    public EligibilityStatus(String studentId, String courseCode,
                             double caPercentage, double attendancePercentage) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.caPercentage = caPercentage;
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isCAEligible() { return caPercentage >= MIN_CA; }
    public boolean isAttendanceEligible() { return attendancePercentage >= MIN_ATTENDANCE; }
    public boolean isFullyEligible() { return isCAEligible() && isAttendanceEligible(); }

    /** Tells the user exactly which rule failed (SRS 3.7). */
    public String getReason() {
        if (isFullyEligible()) return "Eligible";
        StringBuilder sb = new StringBuilder("Not eligible: ");
        if (!isAttendanceEligible()) sb.append("attendance below 80%");
        if (!isAttendanceEligible() && !isCAEligible()) sb.append(" and ");
        if (!isCAEligible()) sb.append("CA below 40%");
        return sb.toString();
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public double getCaPercentage() { return caPercentage; }
    public double getAttendancePercentage() { return attendancePercentage; }
}
