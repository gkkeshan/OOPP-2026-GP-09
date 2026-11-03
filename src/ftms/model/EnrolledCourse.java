package ftms.model;

public class EnrolledCourse {

    private final String courseCode;
    private final String courseName;
    private final int creditHours;
    private final String courseType;
    private final String academicYear;
    private final String semester;
    private final String status;

    public EnrolledCourse(String courseCode, String courseName, int creditHours,
                          String courseType, String academicYear,
                          String semester, String status) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.courseType = courseType;
        this.academicYear = academicYear;
        this.semester = semester;
        this.status = status;
    }

    public String getCourseCode()   { return courseCode; }
    public String getCourseName()   { return courseName; }
    public int getCreditHours()     { return creditHours; }
    public String getCourseType()   { return courseType; }
    public String getAcademicYear() { return academicYear; }
    public String getSemester()     { return semester; }
    public String getStatus()       { return status; }
}