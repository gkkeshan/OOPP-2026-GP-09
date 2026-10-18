package com.fot.model;

import com.fot.exception.InvalidMarkException;

/** A student's mark for one ExamComponent. All marks are out of 100. Maps to table `mark`. */
public class Mark {
    private String markId;
    private String evaluationId;
    private String studentId;
    private double marks;
    private String grade;   // filled in only for the final exam / final subject mark

    public Mark() {}

    public Mark(String markId, String evaluationId, String studentId, double marks) {
        this.markId = markId;
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        setMarks(marks);    // goes through validation
    }

    public String getMarkId() { return markId; }
    public void setMarkId(String markId) { this.markId = markId; }
    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public double getMarks() { return marks; }

    public void setMarks(double marks) {
        if (marks < 0 || marks > 100) {
            throw new InvalidMarkException("Mark must be between 0 and 100");
        }
        this.marks = marks;
    }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}
