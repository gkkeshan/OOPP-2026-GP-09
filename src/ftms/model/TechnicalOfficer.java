package ftms.model;

public class TechnicalOfficer extends User{
    private  int toId;
    private String department;

    public TechnicalOfficer(int userId, String userName, String email, String password, String role, String contactNumber){
        super();
        setRole("TECHNICAL_OFFICER");
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
}
