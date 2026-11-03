package ftms.model;

public class TechnicalOfficer extends User{
    private  int toId;
    private String department;

    public TechnicalOfficer(int userId, String userName, String email, String password, String role, String contactNumber) {
        super(userId, userName, email, password, role, contactNumber);
    }

    public TechnicalOfficer(int toId, String department) {
        this.toId = toId;
        this.department = department;
    }

    public TechnicalOfficer(String userName, String email, String password, String role, String contactNumber, int toId, String department) {
        super(userName, email, password, role, contactNumber);
        this.toId = toId;
        this.department = department;
    }

    public TechnicalOfficer(int userID, String userName, String email, String password, String role, String contactNumber, int toId, String department) {
        super(userID, userName, email, password, role, contactNumber);
        this.toId = toId;
        this.department = department;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "TechnicalOfficer{" +
                "toId=" + toId +
                ", department='" + department + '\'' +
                '}';
    }
}
