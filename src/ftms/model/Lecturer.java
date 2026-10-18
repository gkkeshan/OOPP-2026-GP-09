package ftms.model;

public class Lecturer extends User{
    private int lecturerId;
    private String designation;

    public Lecturer(int userId, String userName, String email, String password, String role, String contactNumber) {
        super();
        setRole("LECTURE");
    }
    public class Lecture{
        public Lecture(int userId, String userName, String email, String password, String role, String contactNumber) {
        }
    }
    public Lecturer(int lecturerId, String designation) {
        this.lecturerId = lecturerId;
        this.designation = designation;
    }

    public Lecturer(String userName, String email, String password, String role, String contactNumber, int lecturerId, String designation) {
        super(userName, email, password, role, contactNumber);
        this.lecturerId = lecturerId;
        this.designation = designation;
    }

    public Lecturer(int userID, String userName, String email, String password, String role, String contactNumber, int lecturerId, String designation) {
        super(userID, userName, email, password, role, contactNumber);
        this.lecturerId = lecturerId;
        this.designation = designation;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "lecturerId=" + lecturerId +
                ", designation='" + designation + '\'' +
                '}';
    }
}
