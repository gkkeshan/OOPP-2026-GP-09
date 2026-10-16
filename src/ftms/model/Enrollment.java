package com.fot.management.models;

import java.time.LocalDate;


public class Enrollment extends BaseEntity {

    private int enrollmentId;
    private int studentId;
    private String courseCode;
    private String academicYear;
    private String semester;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;

    public Enrollment(int enrollmentId, int studentId, String courseCode, String academicYear,
                      String semester, LocalDate enrollmentDate, EnrollmentStatus status) {
        this.enrollmentId = enrollmentId;
        setStudentId(studentId);
        setCourseCode(courseCode);
        setAcademicYear(academicYear);
        setSemester(semester);
        setEnrollmentDate(enrollmentDate);
        setStatus(status);
    }

    public Enrollment(int studentId, String courseCode, String academicYear,
                      String semester, LocalDate enrollmentDate, EnrollmentStatus status) {
        this(0, studentId, courseCode, academicYear, semester, enrollmentDate, status);
    }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) {
        if (studentId <= 0) throw new IllegalArgumentException("A student is required");
        this.studentId = studentId;
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = requireText(courseCode, "Course code", 10).toUpperCase(); }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = requireText(academicYear, "Academic year", 9); }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = requireText(semester, "Semester", 10); }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = requireNotNull(enrollmentDate, "Enrollment date"); }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = requireNotNull(status, "Status"); }

    @Override public Object getId() { return enrollmentId; }
    @Override public String display() { return "Student " + studentId + " -> " + courseCode + " (" + status + ")"; }
}