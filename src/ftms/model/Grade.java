package ftms.model;

public class Grade {
    private String letter;
    private double gradePoint;
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
}
