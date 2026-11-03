package ftms.model;

public class Undergraduate extends User {
    private int studentId;
    private String registration_no;
    private String batch;
    private String academic_year;
    private String batch_id;

    public Undergraduate(int userId, String userName, String email, String password, String role, String contactNumber) {
        super(userId, userName, email, password, role, contactNumber);
    }

    public Undergraduate(int studentId, String registration_no, String batch, String academic_year, String batch_id) {
        this.studentId = studentId;
        this.registration_no = registration_no;
        this.batch = batch;
        this.academic_year = academic_year;
        this.batch_id = batch_id;
    }

    public Undergraduate(String userName, String email, String password, String role, String contactNumber, int studentId, String registration_no, String batch, String academic_year, String batch_id) {
        super(userName, email, password, role, contactNumber);
        this.studentId = studentId;
        this.registration_no = registration_no;
        this.batch = batch;
        this.academic_year = academic_year;
        this.batch_id = batch_id;
    }

    public Undergraduate(int userID, String userName, String email, String password, String role, String contactNumber, int studentId, String registration_no, String batch, String academic_year, String batch_id) {
        super(userID, userName, email, password, role, contactNumber);
        this.studentId = studentId;
        this.registration_no = registration_no;
        this.batch = batch;
        this.academic_year = academic_year;
        this.batch_id = batch_id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRegistration_no() {
        return registration_no;
    }

    public void setRegistration_no(String registration_no) {
        this.registration_no = registration_no;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }

    @Override
    public String toString() {
        return "Undergraduate{" +
                "studentId=" + studentId +
                ", registration_no='" + registration_no + '\'' +
                ", batch='" + batch + '\'' +
                ", academic_year='" + academic_year + '\'' +
                ", batch_id='" + batch_id + '\'' +
                '}';
    }
}
