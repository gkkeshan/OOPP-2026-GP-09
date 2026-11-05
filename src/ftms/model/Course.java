package ftms.model;


public class Course extends BaseEntity {

    private String courseCode;
    private String courseName;
    private int creditHours;
    private boolean hasTheory;
    private boolean hasPractical;
    private int lecturerId;
    private int departmentId;
    private int level;
    private int batchId;

    public Course(String courseCode, String courseName, int creditHours,
                  boolean hasTheory, boolean hasPractical, int lecturerId, int departmentId,
                  int level, int batchId) {
        setCourseCode(courseCode);
        setCourseName(courseName);
        setCreditHours(creditHours);
        setCourseType(hasTheory, hasPractical);
        setLecturerId(lecturerId);
        setDepartmentId(departmentId);
        setLevel(level);
        setBatchId(batchId);
    }

    public static int levelFromCode(String courseCode) {
        String code = requireText(courseCode, "Course code", 10).toUpperCase();
        if (code.length() < 7 || !Character.isDigit(code.charAt(3))) {
            throw new IllegalArgumentException("Course code must contain a level digit, e.g. ICT2132");
        }
        int level = Character.getNumericValue(code.charAt(3));
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Course level must be between 1 and 4");
        }
        return level;
    }

    public static int creditFromCode(String courseCode) {
        String code = requireText(courseCode, "Course code", 10).toUpperCase();
        if (code.length() < 7 || !Character.isDigit(code.charAt(code.length() - 1))) {
            throw new IllegalArgumentException("Course code must end with a credit digit, e.g. ICT2132");
        }
        int credit = Character.getNumericValue(code.charAt(code.length() - 1));
        if (credit < 1 || credit > 6) {
            throw new IllegalArgumentException("Course credit must be between 1 and 6");
        }
        return credit;
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

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = requireInRange(level, 1, 4, "Course level"); }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) {
        if (batchId <= 0) throw new IllegalArgumentException("A course must belong to a batch");
        this.batchId = batchId;
    }

    @Override public Object getId() { return courseCode; }
    @Override public String display() { return courseCode + " - " + courseName; }
}