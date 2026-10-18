package ftms.model;

import exception.InvalidMarkException;

/** A student's mark (out of 100) for one Evaluation. Table: mark. */
public class Mark {
    private String markId;
    private String evaluationId;   // links to Evaluation by ID only
    private String studentId;      // links to Undergraduate by ID only
    private double marks;
    private String grade;

    public Mark() {}

    public Mark(String markId, String evaluationId, String studentId, double marks) {
        this.markId = markId;
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        setMarks(marks);           // validated
    }

    public String getMarkId() { return markId; }
    public void setMarkId(String markId) { this.markId = markId; }
    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public double getMarks() { return marks; }

    public void setMarks(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100) {
            throw new InvalidMarkException("Mark must be between 0 and 100");
        }
        this.marks = marks;
    }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    /** Letter grade from the UGC scale (SRS 7.2). Lower bounds, so 79.5 -> A. */
    public String calculateGrade() {
        if (marks >= 80) return "A+";
        if (marks >= 75) return "A";
        if (marks >= 70) return "A-";
        if (marks >= 65) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 55) return "B-";
        if (marks >= 50) return "C+";
        if (marks >= 45) return "C";
        if (marks >= 40) return "C-";
        if (marks >= 30) return "D";
        return "E";
    }
}
