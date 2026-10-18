package ftms.model;

public class SemesterResult {
    private String resultId;
    private String studentId;
    private String academicYear;
    private String semester;
    private double totalCredits;
    private double sgpa;
    private double cgpa;

    public SemesterResult() {}

    public SemesterResult(String resultId, String studentId, String academicYear, String semester,
                         double totalCredits, double sgpa, double cgpa) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.totalCredits = totalCredits;
        this.sgpa = sgpa;
        this.cgpa = cgpa;
    }

    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public double getTotalCredits() { return totalCredits; }
    public void setTotalCredits(double totalCredits) { this.totalCredits = totalCredits; }
    public double getSgpa() { return sgpa; }
    public void setSgpa(double sgpa) { this.sgpa = sgpa; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
}
