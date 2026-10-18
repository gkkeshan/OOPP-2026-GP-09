package model;

/** One assessment of a course (quiz, assignment, mid, final). Table: evaluation. */
public class Evaluation {
    private String evaluationId;
    private String courseCode;
    private String evaluationName;
    private int maxMarks;
    private double weight;     // % this assessment contributes
    private boolean isCA;      // true = counts toward CA, false = final exam

    public Evaluation() {}

    public Evaluation(String evaluationId, String courseCode, String evaluationName,
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
    public void setCA(boolean isCA) { this.isCA = isCA; }
}
