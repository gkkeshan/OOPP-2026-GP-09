package com.fot.model;

/** One assessment of a course (quiz, assignment, mid, final). Maps to table `evaluation`. */
public class ExamComponent {
    private String evaluationId;
    private String courseCode;
    private String evaluationName;
    private int maxMarks;
    private double weight;      // contribution to the course total, in percent
    private boolean isCA;       // true = counts toward CA, false = final exam

    public ExamComponent() {}

    public ExamComponent(String evaluationId, String courseCode, String evaluationName,
                         int maxMarks, double weight, boolean isCA) {
        this.evaluationId = evaluationId;
        this.courseCode = courseCode;
        this.evaluationName = evaluationName;
        this.maxMarks = maxMarks;
        this.weight = weight;
        this.isCA = isCA;
    }

    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getEvaluationName() { return evaluationName; }
    public void setEvaluationName(String evaluationName) { this.evaluationName = evaluationName; }
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public boolean isCA() { return isCA; }
    public void setCA(boolean CA) { isCA = CA; }

    @Override
    public String toString() {
        return evaluationName + " (" + courseCode + ", " + weight + "%)";
    }
}
