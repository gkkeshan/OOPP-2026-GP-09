package ftms.model;

public class Admin extends User {
    private int adminId;

    public Admin(int adminId) {
        this.adminId = adminId;
    }

    public Admin( int userId, String userName, String email, String password, String role, String contactNumber){
        super(userId,userName, email, password, role, contactNumber);
    }

    public Admin(int userID, String userName, String email, String password, String role, String contactNumber, int adminId) {
        super(userID, userName, email, password, role, contactNumber);
        this.adminId = adminId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                '}';
    }
}
