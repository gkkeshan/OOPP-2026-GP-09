package com.fot.model;

/** One row of the UGC grading scale (see SRS 7.2). Maps to table `grade_scale`. */
public class Grade {
    private String letter;        // A+, A, ... E
    private double gradePoint;    // 4.00 ... 0.00
    private double minMarks;
    private double maxMarks;
    private boolean pass;

    public Grade() {}

    public Grade(String letter, double gradePoint, double minMarks, double maxMarks, boolean pass) {
        this.letter = letter;
        this.gradePoint = gradePoint;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
        this.pass = pass;
    }

    public String getLetter() { return letter; }
    public void setLetter(String letter) { this.letter = letter; }
    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }
    public double getMinMarks() { return minMarks; }
    public void setMinMarks(double minMarks) { this.minMarks = minMarks; }
    public double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }
    public boolean isPass() { return pass; }
    public void setPass(boolean pass) { this.pass = pass; }

    @Override
    public String toString() { return letter + " (" + gradePoint + ")"; }
}
