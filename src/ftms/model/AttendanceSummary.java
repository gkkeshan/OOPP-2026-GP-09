
package ftms.model;

public class AttendanceSummary {

    private int studentId;
    private String courseCode;

    private int theoryPresent;
    private int theoryTotal;

    private int practicalPresent;
    private int practicalTotal;

    private double theoryPercentage;
    private double practicalPercentage;
    private double combinedPercentage;

    public AttendanceSummary() {
    }

    public AttendanceSummary(int studentId, String courseCode,
                             int theoryPresent, int theoryTotal,
                             int practicalPresent, int practicalTotal) {
        this.studentId = studentId;
        this.courseCode = courseCode;

        setTheoryCounts(theoryPresent, theoryTotal);
        setPracticalCounts(practicalPresent, practicalTotal);
        calculatePercentages();
    }

    public void setTheoryCounts(int present, int total) {
        validateCounts(present, total);

        this.theoryPresent = present;
        this.theoryTotal = total;
        calculatePercentages();
    }

    public void setPracticalCounts(int present, int total) {
        validateCounts(present, total);

        this.practicalPresent = present;
        this.practicalTotal = total;
        calculatePercentages();
    }

    private void validateCounts(int present, int total) {
        if (present < 0 || total < 0 || present > total) {
            throw new IllegalArgumentException(
                    "Invalid attendance counts");
        }
    }

    private void calculatePercentages() {
        if (theoryTotal == 0) {
            theoryPercentage = 0;
        } else {
            theoryPercentage =
                    (theoryPresent * 100.0) / theoryTotal;
        }

        if (practicalTotal == 0) {
            practicalPercentage = 0;
        } else {
            practicalPercentage =
                    (practicalPresent * 100.0) / practicalTotal;
        }

        int totalSessions = theoryTotal + practicalTotal;
        int presentSessions = theoryPresent + practicalPresent;

        if (totalSessions == 0) {
            combinedPercentage = 0;
        } else {
            combinedPercentage =
                    (presentSessions * 100.0) / totalSessions;
        }
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getTheoryPresent() {
        return theoryPresent;
    }

    public int getTheoryTotal() {
        return theoryTotal;
    }

    public int getPracticalPresent() {
        return practicalPresent;
    }

    public int getPracticalTotal() {
        return practicalTotal;
    }

    public double getTheoryPercentage() {
        return theoryPercentage;
    }

    public double getPracticalPercentage() {
        return practicalPercentage;
    }

    public double getCombinedPercentage() {
        return combinedPercentage;
    }
}