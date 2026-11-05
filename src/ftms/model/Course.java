package ftms.model;


public class Course extends BaseEntity {

    private String courseCode;
    private String courseName;
    private int creditHours;
    private boolean hasTheory;
    private boolean hasPractical;
    private int lecturerId;
    private int departmentId;

    public Course(String courseCode, String courseName, int creditHours,
                  boolean hasTheory, boolean hasPractical, int lecturerId, int departmentId) {
        setCourseCode(courseCode);
        setCourseName(courseName);
        setCreditHours(creditHours);
        setCourseType(hasTheory, hasPractical);
        setLecturerId(lecturerId);
        setDepartmentId(departmentId);
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) {
        this.courseCode = requireText(courseCode, "Course code", 10).toUpperCase();
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = requireText(courseName, "Course name", 100); }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = requireInRange(creditHours, 1, 6, "Credit hours"); }

    public boolean isHasTheory() { return hasTheory; }
    public boolean isHasPractical() { return hasPractical; }


    public void setCourseType(boolean hasTheory, boolean hasPractical) {
        if (!hasTheory && !hasPractical) {
            throw new IllegalArgumentException("A course must have a theory part, a practical part, or both");
        }
        this.hasTheory = hasTheory;
        this.hasPractical = hasPractical;
    }


    public String getCourseType() {
        if (hasTheory && hasPractical) return "Theory + Practical";
        return hasTheory ? "Theory" : "Practical";
    }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) {
        if (lecturerId <= 0) throw new IllegalArgumentException("A course must have a lecturer");
        this.lecturerId = lecturerId;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) {
        if (departmentId <= 0) throw new IllegalArgumentException("A course must belong to a department");
        this.departmentId = departmentId;
    }

    @Override public Object getId() { return courseCode; }
    @Override public String display() { return courseCode + " - " + courseName; }
}